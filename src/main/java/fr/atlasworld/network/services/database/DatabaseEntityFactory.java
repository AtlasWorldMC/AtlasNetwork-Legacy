package fr.atlasworld.network.services.database;

import fr.atlasworld.network.services.database.entities.DatabaseData;
import fr.atlasworld.network.services.database.entities.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface DatabaseEntityFactory<T extends DatabaseEntity<T>> {
    @NotNull T create(DatabaseData data, UUID id);
}
