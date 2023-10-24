package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.logging.LogUtils;
import io.netty.util.ResourceLeakDetector;

import java.util.Arrays;
import java.util.List;

/**
 * Launch Arguments Parser
 */
public class LaunchArgs {
    /**
     * Starts Network in development environment
     */
    public static final String DEV_ENV_ARG = "-devEnv";

    /**
     * Enables Network's database monitoring, this will log every command it sends to the database.
     */
    public static final String DATABASE_MONITORING = "-enableDatabaseMonitoring";

    private boolean devEnv;
    private boolean databaseMonitoring;

    private LaunchArgs(String[] args) {
        List<String> argsList = Arrays.asList(args);
        this.devEnv = argsList.contains(DEV_ENV_ARG);
        this.databaseMonitoring = argsList.contains(DATABASE_MONITORING);

        //Messages
        if (this.devEnv) {
            AtlasNetwork.logger.warn("Launching AtlasNetwork in dev environment. Remove '{}' launch argument to launch normally.", DEV_ENV_ARG);
            LogUtils.enableDebugLogging();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
            this.databaseMonitoring = true;
        }

        if (this.databaseMonitoring) {
            AtlasNetwork.logger.info("Database monitoring enabled. Every command will be logged.");
        }
    }

    public boolean isDevEnv() {
        return this.devEnv;
    }

    public boolean databaseMonitoringEnabled() {
        return this.databaseMonitoring;
    }

    private static LaunchArgs launchArgs;

    public static LaunchArgs getLaunchArgs() {
        if (launchArgs == null) {
            throw new UnsupportedOperationException("Launch Arguments hasn't been initialized yet!");
        }

        return launchArgs;
    }

    public static void initialize(String[] args) {
        if (launchArgs != null) {
            throw new UnsupportedOperationException("Launch arguments cannot be initialized multiple times!");
        }

        launchArgs = new LaunchArgs(args);
    }
}
