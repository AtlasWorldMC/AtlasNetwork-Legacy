package fr.atlasworld.network.networking.socket;

import fr.atlasworld.network.networking.exceptions.unchecked.NetworkSocketClosedException;
import fr.atlasworld.network.networking.packet.PacketManager;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

/**
 * Socket Manager, handles the socket
 */
public interface SocketManager {
    /**
     * Binds/starts the socket
     * @return Future async task
     */
    ChannelFuture start();

    /**
     * Unbinds/stops the socket
     */
    void stop();

    /**
     * Returns if the socket is bound or not
     * @return socket connected
     */
    boolean isRunning();

    /**
     * Ensures that the socket is running.
     * @throws fr.atlasworld.network.networking.exceptions.unchecked.NetworkSocketClosedException if the socket is cloased.
     */
    default void ensureRunning() {
        if (!this.isRunning()) {
            throw new NetworkSocketClosedException("Socket closed.");
        }
    }

    /**
     * Returns the socket port
     */
    int getPort();

    /**
     * Returns the socket address
     */
    String getAddress();

    /**
     * Returns the full socket address
     */
    InetSocketAddress getFullAddress();

    /**
     * Returns the packet manager
     * @see PacketManager
     */
    PacketManager getPacketManager();
}
