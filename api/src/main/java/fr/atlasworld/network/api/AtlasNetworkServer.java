package fr.atlasworld.network.api;

import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.api.services.ServiceManager;

public interface AtlasNetworkServer {
    ModuleManager getModuleManager();
    NetworkSocket getSocket();
    ServiceManager getServiceManager();
}
