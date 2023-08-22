package fr.atlasworld.network.database;

import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.panel.ServerInfo;

public interface DatabaseManager {
    Database<AuthProfile> getAuthenticationProfileDatabase() throws DatabaseException;
    Database<ServerInfo> getServerDatabase() throws DatabaseException;

    //Misc
    void close();
}
