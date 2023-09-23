package fr.atlasworld.network.database.entities.authentification;

import fr.atlasworld.network.database.entities.DatabaseEntity;

/**
 * Socket Authentication Profile
 */
public record AuthenticationProfile(String id, String hashedToken) implements DatabaseEntity {
}
