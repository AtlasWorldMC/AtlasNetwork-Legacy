package fr.atlasworld.network.database;

import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.database.entities.server.DatabaseServer;
import fr.atlasworld.network.exceptions.database.DatabaseException;

/**
 * Database Manager interface, handles all the databases
 */
public interface DatabaseManager {
    /**
     * Retrieve the authentication profile database
     * @return authentication profile database
     * @throws DatabaseException if something went wrong
     */
    Database<AuthenticationProfile> getAuthenticationProfileDatabase() throws DatabaseException;

    /**
     * Retrieve the server database
     * @return server database
     * @throws DatabaseException if something went wrong
     */
    Database<DatabaseServer> getServerDatabase() throws DatabaseException;

    /**
     * Closes the connection with the database
     */
    void close();
}
