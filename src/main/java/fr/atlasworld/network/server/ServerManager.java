package fr.atlasworld.network.server;

import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.server.configuration.ServerConfiguration;
import fr.atlasworld.network.server.entities.PanelServer;

import java.util.List;
import java.util.Map;

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
     * Creates a server with a given configuration
     * @param configuration server configuration
     * @param name server name
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createCustomServer(ServerConfiguration configuration, String name) throws PanelException;

    /**
     * Creates a server using the default server configuration
     * @param name server name
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createDefaultServer(String name) throws PanelException;

    /**
     * Creates a server using the default proxy configuration, adds the proxy address to the load balancer too
     * @param name server name
     * @return newly created server
     * @throws PanelException if something goes wrong
     */
    PanelServer createDefaultProxy(String name) throws PanelException;

    /**
     * Gets all the server configurations
     * @return server configurations
     */
    Map<String, ServerConfiguration> getServerConfigurations();
}
