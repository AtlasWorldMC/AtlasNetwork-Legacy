package fr.atlasworld.network.api.networking;

import fr.atlasworld.network.api.exception.networking.unchecked.NetworkSocketClosedException;

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
    default void ensureSocketRunning() {
        if (!this.isRunning()) {
            throw new NetworkSocketClosedException("Socket closed!");
        }
    }

    /**
     * Get the bound address of the socket
     */
    InetSocketAddress getBoundAddress();

    /**
     * Retrieve the network manager
     */
    NetworkManager getNetworkManager();

    /**
     * Stops the socket, Starting the socket can only be done internally.
     */
    void stop();
}
