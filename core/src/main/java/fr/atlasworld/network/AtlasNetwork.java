package fr.atlasworld.network;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.logging.InternalLogUtils;
import fr.atlasworld.network.module.ModuleHandler;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;

public class AtlasNetwork implements AtlasNetworkServer {
    public static final Logger logger = InternalLogUtils.getServerLogger();

    private final ModuleHandler moduleHandler;

    public AtlasNetwork(ModuleHandler moduleHandler) {
        this.moduleHandler = moduleHandler;
    }

    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    @Override
    public ModuleManager getModuleManager() {
        return this.moduleHandler;
    }

    @Override
    public NetworkSocket getSocket() {
        return null;
    }
}
