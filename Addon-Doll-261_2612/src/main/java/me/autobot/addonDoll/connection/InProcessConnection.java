package me.autobot.addonDoll.connection;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import me.autobot.addonDoll.player.ServerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.connection.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InProcessConnection extends Connection {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<UUID, DollConnection> DOLL_CONNECTIONS = new ConcurrentHashMap<>();

    @Override
    public void connect(GameProfile profile, Player caller) {
        // keep permission plugin compatibility, as the old login listener did
        Thread preLogin = new Thread(() -> {
            AsyncPlayerPreLoginEvent preLoginEvent = new AsyncPlayerPreLoginEvent(profile.name(), InetAddress.getLoopbackAddress(), profile.id());
            preLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
            preLoginEvent.setKickMessage("PlayerDoll");
            Bukkit.getPluginManager().callEvent(preLoginEvent);
            PlayerDollAPI.getScheduler().globalTaskDelayed(() -> spawn(profile, caller), 5);
        });
        preLogin.start();
    }

    private void spawn(GameProfile profile, Player caller) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        if (server.getPlayerList().getPlayer(profile.id()) != null) {
            return;
        }
        ServerDoll doll = ServerDoll.callSpawn(profile);
        doll.setup(caller);

        // 1.21.9+: player data load moved from placeNewPlayer into the configuration
        // phase (PrepareSpawnTask) which the in-process spawn skips - load it here
        // (Entity#load restores Pos/Rotation; fresh dolls rely on the PlayerJoin teleport)
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(doll.problemPath(), LOGGER)) {
            Optional<ValueInput> data = server.getPlayerList().loadPlayerData(new NameAndId(profile))
                    .map(tag -> TagValueInput.create(reporter, server.registryAccess(), tag));
            data.ifPresent(doll::load);

            DollConnection connection = new DollConnection(doll);
            DOLL_CONNECTIONS.put(profile.id(), connection);
            // register for Connection#tick (listener tick / disconnection handling)
            server.getConnection().getConnections().add(connection);

            server.getPlayerList().placeNewPlayer(connection, doll, CommonListenerCookie.createInitial(profile, false));

            ServerGamePlayListener gamePlayListener = new ServerGamePlayListener(doll, connection, profile);
            connection.setListener(gamePlayListener);
            doll.connection = gamePlayListener;

            data.ifPresent(tag -> {
                doll.loadAndSpawnEnderPearls(tag);
                doll.loadAndSpawnParentVehicle(tag);
            });
        }
        doll.callDollJoinEvent();
    }

    @Override
    public void shutDown() {
        DOLL_CONNECTIONS.values().forEach(DollConnection::closeChannel);
        DOLL_CONNECTIONS.clear();
    }
}
