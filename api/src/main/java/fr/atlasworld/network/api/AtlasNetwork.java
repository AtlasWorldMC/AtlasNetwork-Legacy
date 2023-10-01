package fr.atlasworld.network.api;

import fr.atlasworld.network.api.concurrent.Future;
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
    
    public static Future<Void> runLater(Runnable runnable, long time) {
        return server.runLater(runnable ,time);
    }
}
