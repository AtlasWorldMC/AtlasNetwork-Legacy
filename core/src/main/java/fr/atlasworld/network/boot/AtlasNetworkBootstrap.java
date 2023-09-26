package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidClassException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidException;
import fr.atlasworld.network.api.module.Module;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.logging.InternalLogUtils;
import fr.atlasworld.network.module.ModuleHandler;
import fr.atlasworld.network.module.ModuleInfo;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class AtlasNetworkBootstrap {
    public static void main(String[] args) {
        LaunchArgs launchArgs = new LaunchArgs(args);
        if (launchArgs.isDevEnv()) {
            InternalLogUtils.enableConsoleDebug();
        }

        AtlasNetwork.logger.info("Starting AtlasNetwork..");
        ModuleHandler moduleHandler = new ModuleHandler();

        AtlasNetwork server = new AtlasNetwork(moduleHandler);
        fr.atlasworld.network.api.AtlasNetwork.setServer(server);

        AtlasNetwork.logger.info("Discovering modules..");
        File modulesDirectory = FileManager.getModuleDirectory();
        if (modulesDirectory.isDirectory()) {
            File[] modules = modulesDirectory.listFiles(file -> file.isFile() || file.getName().endsWith(".jar"));

            if (launchArgs.getAddedModule().length > 0) {
                if (modules != null) {
                    File[] combinedModules = Arrays.copyOf(modules, modules.length + launchArgs.getAddedModule().length);
                    System.arraycopy(launchArgs.getAddedModule(), 0, combinedModules, modules.length, launchArgs.getAddedModule().length);
                    modules = combinedModules;
                } else {
                    modules = launchArgs.getAddedModule();
                }
            }

            if (modules != null) {
                try (URLClassLoader moduleLoader = moduleHandler.loadModulesToClasspath(modules)) {
                    for (File moduleJar : modules) {
                        try {
                            ModuleInfo module = moduleHandler.loadModule(moduleJar);
                            moduleHandler.enableModule(module, moduleLoader);

                            AtlasNetwork.logger.info("'{}' loaded!", module.getName());
                        } catch (ModuleInvalidException e) {
                            AtlasNetwork.logger.error(e.getMessage());
                        } catch (ModuleException e) {
                            AtlasNetwork.logger.error("", e);
                        }
                    }
                } catch (ModuleException | IOException e) {
                    AtlasNetwork.logger.error("Failed to load modules to classpath", e);
                    System.exit(-1);
                }
            }
        } else {
            modulesDirectory.mkdirs();
        }

        AtlasNetwork.logger.info("Loaded {} modules!", moduleHandler.getLoadedModules().size());
    }
}
