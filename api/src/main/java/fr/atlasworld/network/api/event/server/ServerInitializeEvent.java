package fr.atlasworld.network.api.event.server;

import fr.atlasworld.network.api.AtlasNetworkServer;

/**
 * Triggered when AtlasNetwork has finished initializing.
 */
public class ServerInitializeEvent extends ServerEvent {
    public ServerInitializeEvent(AtlasNetworkServer server) {
        super(server);
    }
}
