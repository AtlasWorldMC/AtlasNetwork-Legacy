package fr.atlasworld.network.server.configuration;

import com.google.gson.JsonObject;

public record ServerConfiguration(String id, String version, String image, Location location, Node node, Resources resources, JsonObject variables) {
    public record Location(int nest, int egg) {
    }

    public record Node(Integer[] nodes, String balancer){
    }

    public record Resources(int allocation, int memory, int swap, int cpu, int disk) {
    }
}
