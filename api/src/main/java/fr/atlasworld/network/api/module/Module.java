package fr.atlasworld.network.api.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Module {
    @NotNull String getName();
    @NotNull String getId();
    @NotNull String getVersion();
    @Nullable String description();
}
