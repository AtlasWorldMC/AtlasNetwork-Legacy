package fr.atlasworld.network.server;

import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.server.configuration.ServerConfiguration;
import fr.atlasworld.network.server.entities.PanelServer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Server Manager, Manages the servers on the panel.
 */
public interface ServerManager {
    /**
     * Initializes the manager
     * @throws PanelException if something went wrong
     */
    void initialize() throws PanelException;

    /**
     * Retrieves all the servers
     * @return all the available servers
     */
    List<PanelServer> getServers();

    /**
     * Retrieves a server by an id
     * @param id id of the server
     * @return the server, returns null if no server was found with that id.
     */
    @Nullable PanelServer getServer(UUID id);

    /**
     * Creates a server with a given configuration
     * @param configuration server configuration
     * @param name server name
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createCustomServer(ServerConfiguration configuration, String name) throws PanelException;

    /**
     * Creates a server with a given configuration
     * @param configuration server configuration
     * @param name server name
     * @param requestId id of the request for the server
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createCustomServer(ServerConfiguration configuration, String name, UUID requestId) throws PanelException;

    /**
     * Creates a server using the default server configuration
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createDefaultServer() throws PanelException;

    /**
     * Creates a server using the default server configuration
     * @param requestId id of the request for the server
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createDefaultServer(UUID requestId) throws PanelException;

    /**
     * Creates a server using the default proxy configuration, adds the proxy address to the load balancer too
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createDefaultProxy() throws PanelException;

    /**
     * Creates a server using the default server configuration
     * @param requestId id of the request for the server
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createDefaultProxy(UUID requestId) throws PanelException;

    /**
     * Gets all the server configurations
     * @return server configurations
     */
    Map<String, ServerConfiguration> getServerConfigurations();

    /**
     * Retrieves a configuration with its id
     * @param id config id
     * @return configuration, null if it doesn't exists
     */
    @Nullable ServerConfiguration getConfiguration(String id);

    /**
     * Retrieves the server default configuration
     * @return server default configuration
     */
    ServerConfiguration defaultServerConfiguration();

    /**
     * Retrieves the proxy default configuration
     * @return proxy default configuration
     */
    ServerConfiguration defaultProxyConfiguration();
}
