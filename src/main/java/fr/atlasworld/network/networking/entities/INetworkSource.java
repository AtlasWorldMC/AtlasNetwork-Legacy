package fr.atlasworld.network.networking.entities;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mongodb.lang.Nullable;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public interface INetworkSource {
    /**
     * Sends a packet to the source
     */
    @CanIgnoreReturnValue
    ChannelFuture sendPacket(ByteBuf buffer);

    /**
     * Sends a disconnect packet to the source and terminates the connection
     */
    @CanIgnoreReturnValue
    ChannelFuture disconnect(String reason);

    /**
     * Terminates the connection with the source
     */
    @CanIgnoreReturnValue
    ChannelFuture terminate();

    /**
     * Retrieves the remote address of the source
     */
    InetSocketAddress remoteAddress();

    /**
     * Adds a listener to the source when it disconnects.
     * Terminating a connection with {@link INetworkSource#terminate()} will also trigger this listener.
     */
    void onDisconnect(Consumer<INetworkSource> listener);

    /**
     * Attach data to the source.
     */
    void attachData(String key, Object data);

    /**
     * Retrieve attached data from the source.
     */
    @Nullable
    <T> T getData(String key);

    /**
     * Creates an empty packet buffer.
     */
    PacketByteBuf createPacket();
}
