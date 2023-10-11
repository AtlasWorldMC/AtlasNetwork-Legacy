package fr.atlasworld.network;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.event.server.ServerInitializeEvent;
import fr.atlasworld.network.api.exception.services.ServiceException;
import fr.atlasworld.network.api.exception.services.UndefinedServiceException;
import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.api.services.ServiceManager;
import fr.atlasworld.network.logging.InternalLogUtils;
import fr.atlasworld.network.module.ModuleHandler;
import fr.atlasworld.network.module.ModuleInfo;
import fr.atlasworld.network.networking.SocketManager;
import fr.atlasworld.network.networking.security.SecurityManager;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;

import java.io.File;

public class AtlasNetwork extends ModuleInfo implements AtlasNetworkServer {
    public static final Logger logger = InternalLogUtils.getServerLogger();

    private final ModuleHandler moduleHandler;
    private final SocketManager socketManager;
    private final SecurityManager securityManager;
    private final ServiceManager serviceManager;
    private boolean initialized;

    public AtlasNetwork(ModuleHandler moduleHandler, SocketManager socketManager, SecurityManager securityManager, ServiceManager serviceManager) {
        super("", "AtlasNetwork-Core", "atlas-network-core", "1.0.0", "Core module of AtlasNetwork", new File("/"));
        this.moduleHandler = moduleHandler;
        this.socketManager = socketManager;
        this.securityManager = securityManager;
        this.serviceManager = serviceManager;
    }

    public void initialize() throws ServiceException {
        if (this.initialized) {
            throw new UnsupportedOperationException("AtlasNetwork cannot be initialized multiple times!");
        }

        ChannelFuture connectionFuture = this.socketManager.start().addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                AtlasNetwork.logger.error("Failed to start socket: ", future.cause());
                System.exit(-1);
            }

            AtlasNetwork.logger.info("Socket Started.");
        });

        if (this.serviceManager.getDatabaseService() == null) {
            throw new UndefinedServiceException("Database service has not been defined by any module, Network requires at least one Database driver to be installed.");
        }

        if (!connectionFuture.isDone()) {
            AtlasNetwork.logger.debug("Systems initialized before socket did, blocking main thread.");
            connectionFuture.syncUninterruptibly();
        }

        this.initialized = true;
        AtlasNetwork.logger.info("Initialization finished.");
        this.moduleHandler.callEvent(new ServerInitializeEvent(this));

        AtlasNetwork.logger.info("Ready! Waiting for connections..");
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

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }
}
