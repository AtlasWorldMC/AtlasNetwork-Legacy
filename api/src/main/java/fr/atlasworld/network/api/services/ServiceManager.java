package fr.atlasworld.network.api.services;

import fr.atlasworld.network.api.services.database.DatabaseService;

/**
 * Handles every service of AtlasNetwork, as a driver you should want to register your custom service manager here.
 */
public interface ServiceManager {

    /**
     * Retrieve the database service.
     * @return null if the service has not been defined.
     */
    DatabaseService getDatabaseService();

    /**
     * Registers the database service manager, if your module is a driver for a specific database type you should
     * register it here.
     */
    void registerDatabaseService(DatabaseService service);
}
