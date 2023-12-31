package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.ConfigurationManager;
import fr.atlasworld.network.config.exceptions.ConfigurationException;
import fr.atlasworld.network.config.files.DatabaseConfiguration;
import fr.atlasworld.network.config.files.PanelConfiguration;
import fr.atlasworld.network.config.files.SecurityConfiguration;
import fr.atlasworld.network.config.files.SocketConfiguration;
import fr.atlasworld.network.networking.packet.NetworkPacketManager;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.socket.NetworkSocketManager;
import fr.atlasworld.network.networking.socket.SocketManager;
import fr.atlasworld.network.security.NetworkSecurityManager;
import fr.atlasworld.network.security.SecurityManager;
import fr.atlasworld.network.services.database.DatabaseService;
import fr.atlasworld.network.services.database.mongo.MongoDatabaseService;
import fr.atlasworld.network.services.panel.PanelService;
import fr.atlasworld.network.services.panel.schema.ServerSchemaManager;
import fr.atlasworld.network.services.panel.data.ServerDatabase;
import fr.atlasworld.network.services.panel.pteroq.PteroqPanelService;

public class NetworkBootstrap {
    public static void main(String[] args) {
        try {
            Thread.currentThread().setName("initialization");
            LaunchArgs.initialize(args);

            AtlasNetwork.logger.info("Starting AtlasNetwork..");

            ServerSchemaManager schemaManager = new ServerSchemaManager();
            ConfigurationManager configurationManager = new ConfigurationManager();
            NetworkBootstrap.registerConfigurations(configurationManager);

            try {
                AtlasNetwork.logger.info("Loading configuration..");
                configurationManager.loadConfigurations();
                schemaManager.loadSchemas();
                AtlasNetwork.logger.info("Configuration loaded successfully.");
            } catch (ConfigurationException e) {
                AtlasNetwork.logger.error("Failed to load configuration.");
                NetworkBootstrap.crash(e);
            }

            DatabaseConfiguration databaseConfiguration = configurationManager.getConfiguration("database.json", DatabaseConfiguration.class);
            SecurityConfiguration securityConfiguration = configurationManager.getConfiguration("security.json", SecurityConfiguration.class);
            SocketConfiguration socketConfiguration = configurationManager.getConfiguration("socket.json", SocketConfiguration.class);
            PanelConfiguration panelConfiguration = configurationManager.getConfiguration("panel.json", PanelConfiguration.class);

            DatabaseService databaseService = null;
            PanelService panelService = null;

            try {
                AtlasNetwork.logger.info("Initializing services..");
                databaseService = new MongoDatabaseService(databaseConfiguration);
                panelService = new PteroqPanelService(panelConfiguration,
                        databaseService.getDatabase("servers", ServerDatabase.ServerDatabaseFactory.getFactory()), schemaManager);
            } catch (Exception e) {
                AtlasNetwork.logger.error("Failed to initialize services.");
                NetworkBootstrap.crash(e);
            }

            SecurityManager securityManager = null;
            PacketManager packetManager = null;
            SocketManager socketManager = null;

            try {
                AtlasNetwork.logger.info("Initializing managers..");
                securityManager = new NetworkSecurityManager(securityConfiguration);
                packetManager = new NetworkPacketManager();
                socketManager = new NetworkSocketManager(socketConfiguration, securityManager, packetManager, databaseService);
            } catch (Exception e) {
                AtlasNetwork.logger.error("Failed to initialize managers.");
                NetworkBootstrap.crash(e);
            }

            AtlasNetwork server = new AtlasNetwork(configurationManager, socketManager, securityManager, packetManager, panelService);

            try {
                AtlasNetwork.logger.info("Initializing AtlasNetwork..");
                server.start();
                AtlasNetwork.logger.info("AtlasNetwork Initialized successfully.");
            } catch (Throwable e) {
                AtlasNetwork.logger.error("Could not initialize AtlasNetwork.");
                NetworkBootstrap.crash(e);
            }

            AtlasNetwork.logger.info("Ready! Waiting for connections..");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                AtlasNetwork.logger.info("Stopping AtlasNetwork..");
                server.stop();
                AtlasNetwork.logger.info("Bye, bye!");
            }, "shutdown-hook"));

        } catch (Throwable throwable) {
            AtlasNetwork.logger.error("[FATAL] UNCAUGHT EXCEPTION THROWN: {}", throwable.getMessage());
            NetworkBootstrap.crash(throwable);
        }
    }

    private static void registerConfigurations(ConfigurationManager manager) {
        manager.registerSchema(new SocketConfiguration.SocketConfigurationSchema());
        manager.registerSchema(new DatabaseConfiguration.DatabaseConfigurationSchema());
        manager.registerSchema(new SecurityConfiguration.SecurityConfigurationSchema());
        manager.registerSchema(new PanelConfiguration.PanelConfigurationSchema());
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
