package fr.atlasworld.network.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    private static final Logger rootLogger;

    static {
         rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    public static void enableDebugLogging() {
        rootLogger.setLevel(Level.DEBUG);
    }

    public static org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger("AtlasNetwork");
    }
}
