package fr.atlasworld.network.server;

import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.server.configuration.ServerConfiguration;
import fr.atlasworld.network.server.entities.PanelServer;

import java.util.List;
import java.util.Map;

public interface ServerManager {
    void initialize() throws PanelException;
    List<PanelServer> getServers();
    PanelServer createCustomServer(ServerConfiguration configuration, String name) throws PanelException;
    PanelServer createDefaultServer(String name) throws PanelException;
    PanelServer createDefaultProxy(String name) throws PanelException;
    Map<String, ServerConfiguration> getServerConfigurations();
}
