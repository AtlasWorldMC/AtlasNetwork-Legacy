package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.ConfigurationManager;
import fr.atlasworld.network.config.exceptions.ConfigurationException;
import fr.atlasworld.network.config.files.DatabaseConfiguration;
import fr.atlasworld.network.config.files.SecurityConfiguration;
import fr.atlasworld.network.config.files.SocketConfiguration;

public class NetworkBootstrap {
    public static void main(String[] args) {
        Thread.currentThread().setName("initialization");
        LaunchArgs.initialize(args);

        AtlasNetwork.logger.info("Starting AtlasNetwork..");

        ConfigurationManager configurationManager = new ConfigurationManager();
        NetworkBootstrap.registerConfigurations(configurationManager);

        try {
            configurationManager.loadConfigurations();
        } catch (ConfigurationException e) {
            AtlasNetwork.logger.error("Failed to load configuration.");
            NetworkBootstrap.crash(e);
        }

        AtlasNetwork server = new AtlasNetwork();

        try {
            AtlasNetwork.logger.info("Initializing AtlasNetwork..");
            server.initialize();
        } catch (Exception e) {
            AtlasNetwork.logger.error("Could not initialize AtlasNetwork.");
            NetworkBootstrap.crash(e);
        }

        AtlasNetwork.logger.info("Initialization finished, cleaning up..");

        AtlasNetwork.logger.info("Ready! Waiting for connections..");


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AtlasNetwork.logger.info("Stopping AtlasNetwork..");
        }, "shutdown-hook"));
    }

    private static void registerConfigurations(ConfigurationManager manager) {
        manager.registerSchema(new SocketConfiguration.SocketConfigurationSchema());
        manager.registerSchema(new DatabaseConfiguration.DatabaseConfigurationSchema());
        manager.registerSchema(new SecurityConfiguration.SecurityConfigurationSchema());
    }

    public static void shutdown(String reason) {
        AtlasNetwork.logger.info("AtlasNetwork is shutting down '{}'", reason);
        System.exit(0);
    }

    public static void crash(Throwable throwable) {
        AtlasNetwork.logger.error("AtlasNetwork crashed:", throwable);
        System.exit(-1);
    }
}
