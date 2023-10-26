package fr.atlasworld.network;

import java.util.UUID;

/**
 * Helper Interface for making managing of non-persistent(Like: Data, Session, ect..) objects in the application
 */
public interface NetworkEntity {
    /**
     * Retrieve the unique id of the entity
     */
    UUID id();
}
