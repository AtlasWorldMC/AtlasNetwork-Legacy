package fr.atlasworld.network.api;

import fr.atlasworld.network.api.module.ModuleManager;

public final class AtlasNetwork {
    private static AtlasNetworkServer server;

    public static AtlasNetworkServer getServer() {
        return server;
    }
    
    public static void setServer(AtlasNetworkServer server) {
        if (AtlasNetwork.server != null) {
            throw new UnsupportedOperationException("Cannot redefine server!");
        }

        AtlasNetwork.server = server;
    }

    public static ModuleManager getModuleManager() {
        return server.getModuleManager();
    }
}
