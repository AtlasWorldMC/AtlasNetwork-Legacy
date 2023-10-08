package fr.atlasworld.network.api;

import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.api.services.ServiceManager;

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

    public static NetworkSocket getSocket() {
        return server.getSocket();
    }

    public static ServiceManager getServiceManager() {
        return server.getServiceManager();
    }
}
