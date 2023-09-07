package fr.atlasworld.network.server.entities;

import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;
import fr.atlasworld.network.database.entities.server.DatabaseServer;
import fr.atlasworld.network.server.configuration.ServerConfiguration;

/**
 * Represents a remote server on the panel
 */
public interface PanelServer {
    /**
     * Retrieves the server name
     * @return server name
     */
    String name();

    /**
     * Sets the server name
     * @param name server name
     */
    void name(String name);

    /**
     * Retrieves the server description
     * @return server description
     */
    String description();

    /**
     * Sets the server description
     * @param description server description
     */
    void description(String description);

    /**
     * Retrieves the configuration of the server
     * @return server configuration
     */
    ServerConfiguration getConfiguration();

    /**
     * Retrieves the server database data
     * @return server's data
     */
    DatabaseServer getDatabaseServer();

    /**
     * Retrieves the server status
     * @return server status
     */
    ServerStatus status();

    /**
     * Reinstall the server
     */
    void reinstall();

    /**
     * Deletes the server
     */
    void delete();

    /**
     * Deletes the server
     * @param force force the server deletion
     */
    void delete(boolean force);

    /**
     * Starts the server
     */
    void start();

    /**
     * Restarts the server, if the server is offline it will simply start
     */
    void restart();

    /**
     * Stops the server
     */
    void stop();

    /**
     * Kills the server
     */
    void kill();

    /**
     * Uploads a file to the server
     * @param directory the remote target directory
     * @param uploads the files to upload
     */
    void upload(String directory, UploadAction... uploads);

    /**
     * Adds a listener to the server
     * @param listener the server listener
     */
    void addListener(ClientSocketListenerAdapter listener);

    /**
     * Sends a command to the server
     * @param command command to send
     */
    void sendCommand(String command);
}