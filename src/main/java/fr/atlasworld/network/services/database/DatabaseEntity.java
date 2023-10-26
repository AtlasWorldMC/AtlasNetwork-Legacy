package fr.atlasworld.network.services.database;

import fr.atlasworld.network.NetworkEntity;
import org.jetbrains.annotations.NotNull;

public interface DatabaseEntity<T extends DatabaseEntity<T>> extends NetworkEntity {
    @NotNull DatabaseData asData();
}
