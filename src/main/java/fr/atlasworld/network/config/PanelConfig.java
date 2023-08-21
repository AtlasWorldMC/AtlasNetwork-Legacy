package fr.atlasworld.network.config;

import java.util.List;

public record PanelConfig(String url, String token, long userId, List<EggConfig> eggs, Defaults defaults) {
    public record Defaults(String proxy, String server) {
    }
}