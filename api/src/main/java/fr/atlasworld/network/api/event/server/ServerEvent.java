package fr.atlasworld.network.api.event.server;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.event.components.Event;

/**
 * Server Related event
 */
public class ServerEvent implements Event {
    protected final AtlasNetworkServer server;

    public ServerEvent(AtlasNetworkServer server) {
        this.server = server;
    }

    /**
     * Get the server instance
     * @return server instance
     */
    public AtlasNetworkServer getServer() {
        return server;
    }
}
