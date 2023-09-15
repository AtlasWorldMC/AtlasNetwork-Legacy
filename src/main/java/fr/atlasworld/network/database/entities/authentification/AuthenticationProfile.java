package fr.atlasworld.network.database.entities.authentification;

import fr.atlasworld.network.database.entities.DatabaseEntity;

import java.util.UUID;

/**
 * Socket Authentication Profile
 */
public class AuthenticationProfile implements DatabaseEntity {
    private final UUID id;
    private final String hashedToken;

    public AuthenticationProfile(UUID id, String hashedToken) {
        this.id = id;
        this.hashedToken = hashedToken;
    }

    public UUID getId() {
        return id;
    }

    public String getHashedToken() {
        return hashedToken;
    }
}
