package fr.atlasworld.network.networking.entities;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.UUID;


/**
 * Network client, 'holder' for managing socket channels more easily using Network's Packet based API
 */
public class NetworkClient {
    private final Channel channel;
    private final UUID uuid;

    public NetworkClient(Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;
    }

    /**
     * Sends a packet
     * @param buf packet data
     * @return future result of the async operation
     */
    @CanIgnoreReturnValue
    public ChannelFuture sendPacket(PacketByteBuf buf) {
        return this.channel.writeAndFlush(buf);
    }

    /**
     * Ends connection with the remote
     * @return future result of the async operation
     */
    @CanIgnoreReturnValue
    public ChannelFuture disconnect() {
        return this.channel.disconnect();
    }

    /**
     * Retrieves the remote address of the remote
     * @return remote connection address
     */
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    /**
     * Gets the netty channel
     * @return netty io channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Gets the connection id
     * @return connection id
     */
    public UUID getUuid() {
        return uuid;
    }
}
