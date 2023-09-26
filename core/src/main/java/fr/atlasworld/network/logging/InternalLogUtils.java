package fr.atlasworld.network.logging;

import ch.qos.logback.classic.Level;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.logging.LogUtils;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.LoggerFactory;

public class InternalLogUtils extends LogUtils {
    static {
        ch.qos.logback.classic.Logger mongoLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
    }

    public static void enableConsoleDebug() {
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AtlasNetwork.logger.getName());
        rootLogger.setLevel(Level.DEBUG);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }
}
