package fr.atlasworld.network.database;

import fr.atlasworld.network.entities.Server;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.integration.ptero.PteroServer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface DatabaseManager {
    //Auth Profiles
    @Nullable AuthProfile getAuthProfile(UUID uuid) throws DatabaseException;
    Set<AuthProfile> getAuthProfiles() throws DatabaseException;
    boolean authProfileExists(UUID uuid) throws DatabaseException;
    void saveAuthProfile(AuthProfile profile) throws DatabaseException;
    void deleteAuthProfile(UUID uuid) throws DatabaseException;

    //Server
    @Nullable Server getServer(UUID serverId) throws DatabaseException;
    boolean serverExists(UUID serverId) throws DatabaseException;
    void saveServer(PteroServer server) throws DatabaseException;
    void deleteServer(UUID uuid) throws DatabaseException;

    //Misc
    void close();
}
