package fr.atlasworld.network.api;

import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.networking.NetworkSocket;

public interface AtlasNetworkServer {
    ModuleManager getModuleManager();
    NetworkSocket getSocket();
}
