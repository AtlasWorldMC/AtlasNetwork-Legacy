package fr.atlasworld.network.database.entities.server;

import fr.atlasworld.network.database.entities.DatabaseEntity;

import java.util.UUID;

/**
 * Server stored on the database
 */
public class DatabaseServer implements DatabaseEntity {
    private final String id;
    private final String type;

    public DatabaseServer(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public DatabaseServer(UUID id, String type) {
        this(id.toString(), type);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
