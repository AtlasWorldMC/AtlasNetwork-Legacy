package fr.atlasworld.network.utils;

import fr.atlasworld.network.AtlasNetwork;

import java.util.Arrays;
import java.util.List;

public class LaunchArgs {
    private final boolean devEnv;
    private final boolean forceConfig;

    public LaunchArgs(String[] args) {
        List<String> argsList = Arrays.asList(args);
        this.devEnv = argsList.contains(DEV_ENV_ARG);
        this.forceConfig = argsList.contains(FORCE_CONFIG);

        //Messages
        if (this.devEnv) {
            AtlasNetwork.logger.warn("Launching AtlasNetwork in dev environment. Remove '{}' launch argument to launch normally.", DEV_ENV_ARG);
        }
    }

    public boolean isDevEnv() {
        return devEnv;
    }

    public boolean isForceConfig() {
        return forceConfig;
    }

    //Static Args
    public static final String DEV_ENV_ARG = "-devEnv";
    public static final String FORCE_CONFIG = "-forceConfig";
}
