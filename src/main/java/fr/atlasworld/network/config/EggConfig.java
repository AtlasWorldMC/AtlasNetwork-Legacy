package fr.atlasworld.network.config;

import com.google.gson.JsonObject;

public record EggConfig(String key, int nest, int egg, String image, ResourceConfig resources, JsonObject variables) {
    public record ResourceConfig(int allocations, int node, int memory, int swap, int cpu, int disk) {
    }
}
