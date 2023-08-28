package fr.atlasworld.network.networking.entities;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.UUID;

public class NetworkClient {
    private final Channel channel;
    private final UUID uuid;

    public NetworkClient(Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;
    }

    @CanIgnoreReturnValue
    public ChannelFuture sendPacket(PacketByteBuf buf) {
        return this.channel.writeAndFlush(buf);
    }

    @CanIgnoreReturnValue
    public ChannelFuture disconnect(PacketByteBuf buf) {
        return this.channel.disconnect();
    }

    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    public Channel getChannel() {
        return channel;
    }

    public UUID getUuid() {
        return uuid;
    }
}
