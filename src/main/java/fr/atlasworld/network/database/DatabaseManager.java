package fr.atlasworld.network.database;

import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.database.entities.server.DatabaseServer;
import fr.atlasworld.network.exceptions.database.DatabaseException;

public interface DatabaseManager {
    Database<AuthenticationProfile> getAuthenticationProfileDatabase() throws DatabaseException;
    Database<DatabaseServer> getServerDatabase() throws DatabaseException;

    //Misc
    void close();
}
