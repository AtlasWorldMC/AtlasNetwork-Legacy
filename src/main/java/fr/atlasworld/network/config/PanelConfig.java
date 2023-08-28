package fr.atlasworld.network.config;

public record PanelConfig(String url, String token, long userId, Defaults defaults) {
    public record Defaults(String proxy, String server) {
    }
}
