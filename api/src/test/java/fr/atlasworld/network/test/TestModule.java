package fr.atlasworld.network.test;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.module.NetworkModule;
import fr.atlasworld.network.test.listeners.ServerListener;
import org.slf4j.Logger;

public class TestModule extends NetworkModule {
    private static Logger logger;

    @Override
    protected void initialize(AtlasNetworkServer server) throws ModuleException {
        logger = this.getLogger();
        this.registerListeners(server.getModuleManager());
    }

    private void registerListeners(ModuleManager manager) {
        manager.registerListener(new ServerListener(module), this);
    }

    public static Logger logger() {
        return logger;
    }
}
