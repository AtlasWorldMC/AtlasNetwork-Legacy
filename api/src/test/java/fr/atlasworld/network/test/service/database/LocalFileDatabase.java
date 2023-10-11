package fr.atlasworld.network.test.service.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.atlasworld.network.api.NetworkEntity;
import fr.atlasworld.network.api.exception.services.database.DatabaseDataDoesNotExistException;
import fr.atlasworld.network.api.exception.services.database.DatabaseException;
import fr.atlasworld.network.api.file.loader.JsonFileLoader;
import fr.atlasworld.network.api.services.database.Database;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class LocalFileDatabase<T extends NetworkEntity> implements Database<T> {
    private final JsonFileLoader loader;
    private final Type type;
    private final Map<UUID, T> dataCache;
    private final Gson gson;

    public LocalFileDatabase(JsonFileLoader loader, Type type, Map<UUID, T> dataCache, Gson gson) {
        this.loader = loader;
        this.type = type;
        this.dataCache = dataCache;
        this.gson = gson;

        this.load();
    }

    public LocalFileDatabase(File file, Type type) {
        this(new JsonFileLoader(file), type, new HashMap<>(), new Gson());
    }

    @Override
    public void save(T value) throws DatabaseException {
        this.dataCache.put(value.id(), value);
        this.save();
    }

    @Override
    public void delete(UUID uuid) throws DatabaseException {
        if (!this.has(uuid)) {
            throw new DatabaseDataDoesNotExistException("Data does not exist in json file.");
        }
        this.dataCache.remove(uuid);
    }

    @Override
    public @Nullable T load(UUID uuid) throws DatabaseException {
        return this.dataCache.get(uuid);
    }

    @Override
    public boolean has(UUID uuid) throws DatabaseException {
        return this.dataCache.containsKey(uuid);
    }

    @Override
    public Set<T> getAll() throws DatabaseException {
        return new HashSet<>(this.dataCache.values());
    }

    private void save() {
        JsonObject parentJson = new JsonObject();
        this.dataCache.forEach((id, value) -> parentJson.add(id.toString(), this.gson.toJsonTree(value)));
        this.loader.save(parentJson);
    }

    private void load() {
        JsonObject data = this.loader.load().getAsJsonObject();
        data.entrySet().forEach(entry -> {
            this.dataCache.put(UUID.fromString(entry.getKey()), this.gson.fromJson(entry.getValue(), this.type));
        });
    }
}
