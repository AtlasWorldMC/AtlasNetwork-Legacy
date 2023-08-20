package fr.atlasworld.network.config;

import java.util.List;

public record PanelConfig(String url, String token, EggsConfig eggs) {
    public record EggsConfig(List<EggConfig> eggs) {
    }
}
