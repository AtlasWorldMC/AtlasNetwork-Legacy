package fr.atlasworld.network;

import fr.atlasworld.network.logging.LogUtils;
import org.slf4j.Logger;

public class AtlasNetwork {
    public static final Logger logger = LogUtils.getLogger();
    private static AtlasNetwork instance;

    private boolean initialized = false;

    public AtlasNetwork() {
        if (instance != null) {
            throw new UnsupportedOperationException("Only on AtlasNetwork Server instance can be created!");
        }
    }

    public void initialize() throws Exception {
        if (this.initialized) {
            throw new UnsupportedOperationException("AtlasNetwork cannot be initialized multiple times!");
        }

        this.initialized = true;
        instance = this;
    }


    public static AtlasNetwork getServer() {
        if (instance == null) {
            throw new UnsupportedOperationException("AtlasNetwork did not finish initialization!");
        }

        return instance;
    }
}
