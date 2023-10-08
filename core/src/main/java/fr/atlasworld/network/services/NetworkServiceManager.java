package fr.atlasworld.network.services;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.Priority;
import fr.atlasworld.network.api.services.database.DatabaseService;
import fr.atlasworld.network.api.event.components.EventListener;
import fr.atlasworld.network.api.event.components.Listener;
import fr.atlasworld.network.api.event.server.ServerInitializeEvent;
import fr.atlasworld.network.api.services.ServiceManager;

public class NetworkServiceManager implements ServiceManager, EventListener {
    private boolean servicesDefined = false;

    private DatabaseService database;

    @Override
    public DatabaseService getDatabaseService() {
        if (this.database == null) {
            AtlasNetwork.logger.debug("Database service instance has been accessed while being null.");
        }

        return database;
    }

    @Override
    public void registerDatabaseService(DatabaseService service) {
        if (this.servicesDefined) {
            throw new UnsupportedOperationException("Services can only be registered before the ServerInitializeEvent is triggered!");
        }

        if (this.database != null) {
            AtlasNetwork.logger.warn("Database service has been defined multiple times, this can mean that multiple database drivers are installed!");
        }

        this.database = service;
    }

    @Listener(priority = Priority.HIGHEST)
    private void onServerInitialized(ServerInitializeEvent event) {
        this.servicesDefined = true;
    }
}

