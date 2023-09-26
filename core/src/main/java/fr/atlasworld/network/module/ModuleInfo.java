package fr.atlasworld.network.module;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import fr.atlasworld.network.api.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ModuleInfo implements Module {
    @Expose
    private final String mainClass;
    @Expose
    private final String name;
    @Expose
    private final String id;
    @Expose
    private final String version;
    @Expose
    private final @Nullable String description;
    @Expose
    private final File file;

    public ModuleInfo(@NotNull String mainClass, @NotNull String name, @Nullable String id, @NotNull String version, @Nullable String description, @NotNull File file) {
        Preconditions.checkArgument(mainClass != null && name != null && version != null, "Module mainClass, name and version must be specified!");

        this.mainClass = mainClass;
        this.name = name;
        this.id = id == null ? name.toLowerCase().replace(" ", "_") : id; // If the id is not specified create one from the name
        this.version = version;
        this.description = description;
        this.file = file;
    }

    public String getMainClass() {
        return mainClass;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

    @Override
    public @Nullable String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull File getFile() {
        return this.file;
    }

    public static ModuleInfo createFromJson(JsonObject json, File file) {
        String mainClass = json.has("mainClass") ? json.get("mainClass").getAsString() : null;
        String name = json.has("name") ? json.get("name").getAsString() : null;
        String id = json.has("id") ? json.get("id").getAsString() : null;
        String version = json.has("version") ? json.get("version").getAsString() : null;
        String description = json.has("description") ? json.get("description").getAsString() : null;

        return new ModuleInfo(mainClass, name, id, version, description, file);
    }
}
