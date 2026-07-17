package me.autobot.addonDoll.connection;

import com.mojang.authlib.GameProfile;
import me.autobot.addonDoll.player.ServerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerGamePlayListener extends ServerGamePacketListenerImpl {
    private final ServerDoll doll;

    public ServerGamePlayListener(ServerDoll doll, Connection networkmanager, GameProfile profile) {
        super(doll.level().getServer(), networkmanager, doll, CommonListenerCookie.createInitial(profile, false));
        this.doll = doll;
    }

    @Override
    public boolean hasClientLoaded() {
        return true;
    }

    @Override
    public void tick() {
        if (doll.dollConfig.dollRealPlayerTickUpdate.getValue()) {
            doll.doTick();
        }
        // no keepConnectionAlive: nothing answers keep-alive
    }

    @Override
    public void onDisconnect(DisconnectionDetails disconnectiondetails) {
        InProcessConnection.DOLL_CONNECTIONS.remove(doll.getUUID());
        super.onDisconnect(disconnectiondetails);
    }
}
