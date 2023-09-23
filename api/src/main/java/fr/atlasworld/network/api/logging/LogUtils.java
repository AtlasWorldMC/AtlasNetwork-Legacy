package fr.atlasworld.network.api.logging;

import fr.atlasworld.network.api.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    private static final Logger networkLogger = LoggerFactory.getLogger("AtlasNetwork");

    public static Logger getServerLogger() {
        return LogUtils.networkLogger;
    }

    public static Logger getLogger(Module module) {
        return LoggerFactory.getLogger(module.getName());
    }
}
