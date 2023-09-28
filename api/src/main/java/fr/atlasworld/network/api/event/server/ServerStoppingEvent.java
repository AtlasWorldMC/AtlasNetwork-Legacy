package fr.atlasworld.network.api.event.server;

import fr.atlasworld.network.api.AtlasNetworkServer;

/**
 * Triggered when AtlasNetwork stops.
 */
public class ServerStoppingEvent extends ServerEvent {
    public ServerStoppingEvent(AtlasNetworkServer server) {
        super(server);
    }
}
