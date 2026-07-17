package me.autobot.addonDoll.connection;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.embedded.EmbeddedChannel;
import me.autobot.playerdoll.api.ReflectionUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

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

    public DollConnection() {
        super(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(this);
        this.address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
    }

    @Override
    public void send(Packet<?> packet, ChannelFutureListener callbacks, boolean flush) {
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
