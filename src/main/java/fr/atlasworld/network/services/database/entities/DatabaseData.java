package fr.atlasworld.network.services.database.entities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DatabaseData {
    private final Map<String, Object> data;

    public DatabaseData(Map<String, Object> data) {
        this.data = data;
    }

    public DatabaseData() {
        this(new LinkedHashMap<>());
    }

    public DatabaseData(String key, Object data) {
        this.data = new LinkedHashMap<>();
        this.data.put(key, data);
    }

    public void put(String key, Object data) {
        this.data.put(key, data);
    }

    public boolean has(String key) {
        return this.data.containsKey(key);
    }

    public Object get(String key) {
        return this.data.get(key);
    }

    public Stream<Map.Entry<String, Object>> stream() {
        return this.data.entrySet().stream();
    }

    public Map<String, Object> asMap() {
        return this.data;
    }
}
