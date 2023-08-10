package fr.atlasworld.network.networking.settings;

import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import org.jetbrains.annotations.NotNull;

public interface SocketSettings {
    /**
     * Gets the boostrap for the server
     * @return Server Bootstrap
     */
    @NotNull ServerBootstrap getBootstrap();

    /**
     * Gets the server bind port
     * @return port
     */
    int getPort();

    /**
     * Gets the server bind address
     * @return address
     */
    @NotNull String getAddress();

    /**
     * Gets the session manager of the socket
     * @return Session Manager
     */
    @NotNull SessionManager getSessionManager();

    /**
     * This is called when the socket is stopping, and it's time the clean-up resources
     */
    void cleanUp();
}
