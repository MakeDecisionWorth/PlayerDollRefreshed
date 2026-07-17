package me.autobot.addonDoll.connection;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.embedded.EmbeddedChannel;
import me.autobot.addonDoll.player.ServerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

// In-process connection: no real socket, packets are discarded
public class DollConnection extends Connection {
    private static final Field packetListenerField;

    static {
        packetListenerField = Arrays.stream(Connection.class.getDeclaredFields())
                .filter(field -> field.getName().equals("packetListener"))
                .findFirst()
                .orElseThrow();
        packetListenerField.setAccessible(true);
    }

    private final ServerDoll doll;

    public DollConnection(ServerDoll doll) {
        super(PacketFlow.SERVERBOUND);
        this.doll = doll;
        new EmbeddedChannel(this);
        this.address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
    }

    @Override
    public void send(Packet<?> packet, ChannelFutureListener callbacks, boolean flush) {
        // mirror the old fake client responses (death screen / credits), then discard
        if (packet instanceof ClientboundPlayerCombatKillPacket) {
            PlayerDollAPI.getScheduler().entityTaskDelayed(doll::performRespawn, doll.getBukkitPlayer(), 2);
            PlayerDollAPI.getScheduler().entityTaskDelayed(doll::dollDisconnect, doll.getBukkitPlayer(), 5);
        } else if (packet instanceof ClientboundGameEventPacket gameEvent && gameEvent.getEvent() == ClientboundGameEventPacket.WIN_GAME) {
            PlayerDollAPI.getScheduler().entityTaskDelayed(doll::performRespawn, doll.getBukkitPlayer(), 2);
        }
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> state, T packetListener) {
        // skip pipeline reconfiguration on the embedded channel
        setListener(packetListener);
    }

    @Override
    public void setupOutboundProtocol(ProtocolInfo<?> state) {
    }

    public void setListener(PacketListener listener) {
        ReflectionUtil.setField(packetListenerField, this, listener);
    }

    public void closeChannel() {
        this.channel.close();
    }
}
