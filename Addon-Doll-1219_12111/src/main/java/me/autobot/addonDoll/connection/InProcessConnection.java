package me.autobot.addonDoll.connection;

import com.mojang.authlib.GameProfile;
import me.autobot.addonDoll.player.ServerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.connection.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InProcessConnection extends Connection {
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

        DollConnection connection = new DollConnection(doll);
        DOLL_CONNECTIONS.put(profile.id(), connection);
        // register for Connection#tick (listener tick / disconnection handling)
        server.getConnection().getConnections().add(connection);

        server.getPlayerList().placeNewPlayer(connection, doll, CommonListenerCookie.createInitial(profile, false));

        ServerGamePlayListener gamePlayListener = new ServerGamePlayListener(doll, connection, profile);
        connection.setListener(gamePlayListener);
        doll.connection = gamePlayListener;
        doll.callDollJoinEvent();
    }

    @Override
    public void shutDown() {
        DOLL_CONNECTIONS.values().forEach(DollConnection::closeChannel);
        DOLL_CONNECTIONS.clear();
    }
}
