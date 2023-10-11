package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.event.server.ServerStoppingEvent;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidException;
import fr.atlasworld.network.config.Config;
import fr.atlasworld.network.api.file.FileManager;
import fr.atlasworld.network.logging.InternalLogUtils;
import fr.atlasworld.network.module.ModuleHandler;
import fr.atlasworld.network.module.ModuleInfo;
import fr.atlasworld.network.networking.SocketManager;
import fr.atlasworld.network.networking.packet.ExecutorPoolPacketManager;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.socket.NetworkSocketManager;
import fr.atlasworld.network.networking.security.NetworkSecurityManager;
import fr.atlasworld.network.networking.security.SecurityManager;
import fr.atlasworld.network.services.NetworkServiceManager;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;

public class AtlasNetworkBootstrap {
    public static void main(String[] args) {
        LaunchArgs launchArgs = new LaunchArgs(args);
        if (launchArgs.isDevEnv()) {
            InternalLogUtils.enableConsoleDebug();
        }

        Config config = Config.getConfig();

        AtlasNetwork.logger.info("Starting AtlasNetwork..");

        try {
            // Managers
            ModuleHandler moduleHandler = new ModuleHandler();
            SecurityManager securityManager = new NetworkSecurityManager(config);
            PacketManager packetManager = new ExecutorPoolPacketManager();
            SocketManager socketManager = new NetworkSocketManager(securityManager, packetManager, config.socketPort(), config.socketHost());
            NetworkServiceManager serviceManager = new NetworkServiceManager();

            // Server
            AtlasNetwork server = new AtlasNetwork(moduleHandler, socketManager, securityManager, serviceManager);
            fr.atlasworld.network.api.AtlasNetwork.setServer(server);

            moduleHandler.registerListener(serviceManager, server);

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
                                AtlasNetwork.logger.error("Unable to find a module", e);
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
            server.initialize();

            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                AtlasNetwork.logger.info("AtlasNetwork is shutting down.");

                AtlasNetwork.logger.info("Notifying modules..");
                moduleHandler.callEvent(new ServerStoppingEvent(server));

                AtlasNetwork.logger.info("Shutting socket down..");
                server.getSocket().stop();
                AtlasNetwork.logger.info("Socket stopped.");

                AtlasNetwork.logger.info("Bye bye!");
            }, "AtlasNetwork-Bootstrap-Shutdown-Hook"));

        } catch (Exception e) {
            AtlasNetwork.logger.error("Unable to start AtlasNetwork", e);
            System.exit(-1);
        }
    }
}
