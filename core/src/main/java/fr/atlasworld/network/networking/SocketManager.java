package fr.atlasworld.network.networking;

import fr.atlasworld.network.api.networking.NetworkSocket;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

public interface SocketManager extends NetworkSocket {
    /**
     * Binds/starts the socket
     * @return Future async task
     */
    ChannelFuture start();

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

    /**
     * Returns the full bind address of the socket
     * @return address
     */
    InetSocketAddress localAddress();
}
