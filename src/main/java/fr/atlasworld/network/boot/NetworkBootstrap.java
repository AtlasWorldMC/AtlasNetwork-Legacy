package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;

public class NetworkBootstrap {
    public static void main(String[] args) {
        Thread.currentThread().setName("initialization");
        LaunchArgs.initialize(args);

        AtlasNetwork.logger.info("Starting AtlasNetwork..");

        AtlasNetwork server = new AtlasNetwork();

        try {
            AtlasNetwork.logger.info("Initializing AtlasNetwork..");
            server.initialize();
        } catch (Exception e) {
            AtlasNetwork.logger.error("Could not initialize AtlasNetwork.");
            NetworkBootstrap.crash(e);
        }

        AtlasNetwork.logger.info("Initialization finished, cleaning up..");
        AtlasNetwork.logger.info("Freeing memory of all unused objects..");
        Runtime.getRuntime().gc();

        AtlasNetwork.logger.info("Ready! Waiting for connections..");


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AtlasNetwork.logger.info("Stopping AtlasNetwork..");
        }, "shutdown-hook"));
    }

    public static void shutdown(String reason) {
        AtlasNetwork.logger.info("AtlasNetwork is shutting down '{}'", reason);
        System.exit(0);
    }

    public static void crash(Throwable throwable) {
        AtlasNetwork.logger.error("AtlasNetwork crashed: ", throwable);
        System.exit(-1);
    }
}
