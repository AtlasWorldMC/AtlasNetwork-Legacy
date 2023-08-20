package fr.atlasworld.network.config;

public record PanelConfig(String url, String token, EggsConfig eggs) {
    public record EggsConfig(EggConfig velocity, EggConfig papermc, EggConfig redis) {
    }
}
