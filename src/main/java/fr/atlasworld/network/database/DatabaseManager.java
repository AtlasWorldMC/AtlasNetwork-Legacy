package fr.atlasworld.network.database;

import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;

public interface DatabaseManager {
    Database<AuthProfile> getAuthenticationProfileDatabase() throws DatabaseException;

    //Misc
    void close();
}
