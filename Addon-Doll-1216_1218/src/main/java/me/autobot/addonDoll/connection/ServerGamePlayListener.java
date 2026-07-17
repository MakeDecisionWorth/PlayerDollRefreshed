package me.autobot.addonDoll.connection;

import com.mojang.authlib.GameProfile;
import me.autobot.addonDoll.player.ServerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerGamePlayListener extends ServerGamePacketListenerImpl {
    private final ServerDoll doll;

    public ServerGamePlayListener(ServerDoll doll, Connection networkmanager, GameProfile profile) {
        super(doll.getServer(), networkmanager, doll, CommonListenerCookie.createInitial(profile, false));
        this.doll = doll;
    }

    @Override
    public void tick() {
        if (doll.dollConfig.dollRealPlayerTickUpdate.getValue()) {
            doll.doTick();
        }
        // no keepConnectionAlive: nothing answers keep-alive
    }

    @Override
    public void send(Packet<?> packet) {
        // mirror the old fake client responses (death screen / credits)
        if (packet instanceof ClientboundPlayerCombatKillPacket) {
            PlayerDollAPI.getScheduler().entityTaskDelayed(this::performRespawn, doll.getBukkitPlayer(), 2);
            PlayerDollAPI.getScheduler().entityTaskDelayed(doll::dollDisconnect, doll.getBukkitPlayer(), 5);
        } else if (packet instanceof ClientboundGameEventPacket gameEvent && gameEvent.getEvent() == ClientboundGameEventPacket.WIN_GAME) {
            PlayerDollAPI.getScheduler().entityTaskDelayed(this::performRespawn, doll.getBukkitPlayer(), 2);
        }
        super.send(packet);
    }

    @Override
    public void onDisconnect(DisconnectionDetails disconnectiondetails) {
        InProcessConnection.DOLL_CONNECTIONS.remove(doll.getUUID());
        super.onDisconnect(disconnectiondetails);
    }

    private void performRespawn() {
        handleClientCommand(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
    }
}
