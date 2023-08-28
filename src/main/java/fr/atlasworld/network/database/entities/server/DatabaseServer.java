package fr.atlasworld.network.database.entities.server;

import fr.atlasworld.network.database.entities.DatabaseEntity;

import java.util.UUID;

public class DatabaseServer implements DatabaseEntity {
    private final UUID id;
    private final String type;

    public DatabaseServer(UUID id, String type) {
        this.id = id;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
