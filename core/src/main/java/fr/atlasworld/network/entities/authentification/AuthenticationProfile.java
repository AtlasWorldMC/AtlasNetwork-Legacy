package fr.atlasworld.network.entities.authentification;

import fr.atlasworld.network.api.NetworkEntity;

import java.util.UUID;

/**
 * Socket Authentication Profile
 */
public record AuthenticationProfile(UUID id, String hash) implements NetworkEntity {
}
