package fr.atlasworld.network.test.entities;

import fr.atlasworld.network.api.NetworkEntity;

import java.util.UUID;

public class TimeData implements NetworkEntity {
    public static final UUID DATA_ID = UUID.fromString("a204a501-7da2-4b93-b378-17edecc8bbbf");

    private final long savedTime;

    public TimeData(long savedTime) {
        this.savedTime = savedTime;
    }

    @Override
    public UUID id() {
        return DATA_ID;
    }

    public long getSavedTime() {
        return savedTime;
    }
}
