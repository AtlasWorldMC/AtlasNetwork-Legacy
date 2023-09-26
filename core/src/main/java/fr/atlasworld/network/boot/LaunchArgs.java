package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.AtlasNetworkOld;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Launch Arguments Parser
 */
public class LaunchArgs {
    private final boolean devEnv;
    private final File[] addedModules;

    public LaunchArgs(String[] args) {
        List<String> argsList = Arrays.asList(args);
        this.devEnv = argsList.contains(DEV_ENV_ARG);

        if (this.devEnv) {
            AtlasNetworkOld.logger.warn("Launching AtlasNetwork in dev environment. Remove '{}' launch argument to launch normally.", DEV_ENV_ARG);
        }

        if (argsList.contains(ADD_MODULE_ARG)) {
            int addModuleIndex = argsList.indexOf(ADD_MODULE_ARG);

            if (addModuleIndex < argsList.size() - 1) {
                String modulePaths = argsList.get(addModuleIndex + 1);
                String[] modulePathsArray = modulePaths.split(";");

                List<File> moduleFiles = new ArrayList<>();
                for (String modulePath : modulePathsArray) {
                    if (!modulePath.endsWith(".jar")) {
                        AtlasNetwork.logger.error("Only jars can be added to the module class path: {}", modulePath);
                    }

                    File moduleFile = new File(modulePath);
                    if (moduleFile.exists() && moduleFile.isFile()) {
                        moduleFiles.add(moduleFile);
                    } else {
                        AtlasNetwork.logger.error("Could not find specified module : {}", modulePath);
                    }
                }

                this.addedModules = moduleFiles.toArray(new File[0]);
            } else {
                this.addedModules = new File[0];
                AtlasNetwork.logger.warn("No modules provided after '{}' argument, Usage: '{} path/to/file.jar'", DEV_ENV_ARG, DEV_ENV_ARG);
            }
        } else {
            this.addedModules = new File[0];
        }
    }

    public File[] getAddedModule() {
        return this.addedModules;
    }

    public boolean isDevEnv() {
        return devEnv;
    }

    /**
     * Starts Network in development environment
     */
    public static final String DEV_ENV_ARG = "-devEnv";

    /**
     * Adds a module to Network without requiring it to be in the 'modules' folder
     */
    public static final String ADD_MODULE_ARG = "-add-module";
}
