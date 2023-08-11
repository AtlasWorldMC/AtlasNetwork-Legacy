package fr.atlasworld.network.database;

import fr.atlasworld.network.entities.auth.AuthProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface DatabaseManager {
    //Auth Profiles
    @Nullable AuthProfile getAuthProfile(UUID uuid);
    Set<AuthProfile> getAuthProfiles();
    boolean authProfileExists(UUID uuid);
    void saveAuthProfile(AuthProfile profile);
    void deleteAuthProfile(UUID uuid);
}
