package fr.atlasworld.network;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.logging.InternalLogUtils;
import fr.atlasworld.network.module.ModuleHandler;
import fr.atlasworld.network.networking.SocketManager;
import fr.atlasworld.network.security.SecurityManager;
import org.slf4j.Logger;

public class AtlasNetwork implements AtlasNetworkServer {
    public static final Logger logger = InternalLogUtils.getServerLogger();

    private final ModuleHandler moduleHandler;
    private final SocketManager socketManager;
    private final SecurityManager securityManager;

    public AtlasNetwork(ModuleHandler moduleHandler, SocketManager socketManager, SecurityManager securityManager) {
        this.moduleHandler = moduleHandler;
        this.socketManager = socketManager;
        this.securityManager = securityManager;
    }

    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    @Override
    public ModuleManager getModuleManager() {
        return this.moduleHandler;
    }

    @Override
    public NetworkSocket getSocket() {
        return this.socketManager;
    }
}
