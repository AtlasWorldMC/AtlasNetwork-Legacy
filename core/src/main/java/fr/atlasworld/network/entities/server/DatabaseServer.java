package fr.atlasworld.network.entities.server;

import fr.atlasworld.network.api.NetworkEntity;

import java.util.UUID;

/**
 * Server stored on the database
 */
public record DatabaseServer(UUID id, String type) implements NetworkEntity {
}
