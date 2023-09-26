package fr.atlasworld.network.test;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.module.NetworkModule;
import org.slf4j.Logger;

public class TestModule extends NetworkModule {
    private static Logger logger;

    @Override
    protected void initialize(AtlasNetworkServer server) throws ModuleException {
        logger = this.getLogger();

    }

    public static Logger logger() {
        return logger;
    }
}
