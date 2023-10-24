package fr.atlasworld.network.services.database;

import fr.atlasworld.network.INetworkEntity;
import org.jetbrains.annotations.NotNull;

public interface IDatabaseEntity<T extends IDatabaseEntity<T>> extends INetworkEntity {
    @NotNull DatabaseData asData();
}
