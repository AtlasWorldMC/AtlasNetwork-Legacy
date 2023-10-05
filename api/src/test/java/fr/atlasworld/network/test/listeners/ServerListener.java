package fr.atlasworld.network.test.listeners;

import fr.atlasworld.network.api.event.components.EventListener;
import fr.atlasworld.network.api.event.components.Listener;
import fr.atlasworld.network.api.event.server.ServerInitializeEvent;
import fr.atlasworld.network.api.event.server.ServerStoppingEvent;
import fr.atlasworld.network.test.TestModule;

public class ServerListener implements EventListener {
    @Listener
    public void onServerInit(ServerInitializeEvent event) {
        TestModule.logger().info("Detected AtlasNetwork initialization has finished.");
        System.exit(0);
    }

    @Listener
    public void onServerStopping(ServerStoppingEvent event) {
        TestModule.logger().info("Shutting Test Module..");
    }
}