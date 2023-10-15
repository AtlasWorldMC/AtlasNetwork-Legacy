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


    private final boolean devEnv;

    private LaunchArgs(String[] lau) {
        List<String> argsList = Arrays.asList(lau);
        this.devEnv = argsList.contains(DEV_ENV_ARG);

        //Messages
        if (this.devEnv) {
            AtlasNetwork.logger.warn("Launching AtlasNetwork in dev environment. Remove '{}' launch argument to launch normally.", DEV_ENV_ARG);
            LogUtils.enableDebugLogging();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        }
    }

    public boolean isDevEnv() {
        return devEnv;
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
