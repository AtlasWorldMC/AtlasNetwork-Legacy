package fr.atlasworld.network.api.networking;

import java.net.InetSocketAddress;

public interface NetworkSocket {
    /**
     * Checks if the socket is running.
     */
    boolean isRunning();

    /**
     * Ensures that the socket is running
     * @throws fr.atlasworld.network.api.exception.networking.unchecked.NetworkSocketClosedException
     *          in case that the socket isn't running.
     */
    void ensureSocketRunning();

    /**
     * Get the bound address of the socket
     */
    InetSocketAddress getBoundAddress();

    /**
     * Retrieve the network manager
     */
    NetworkManager getNetworkManager();
}
