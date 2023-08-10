package fr.atlasworld.network.networking.socket;

import io.netty.channel.ChannelFuture;

public interface SocketManager {
    /**
     * Binds/starts the socket
     * @return Future async task
     */
    ChannelFuture bind();

    /**
     * Unbinds/stops the socket
     */
    void unbind();

    /**
     * Returns if the socket is bound or not
     * @return socket connected
     */
    boolean isBound();

    /**
     * Returns the socket port
     * @return port
     */
    int getPort();

    /**
     * Returns the socket address
     * @return address
     */
    String getAddress();
}
