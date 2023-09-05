package fr.atlasworld.network.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;

public record Config(String socketHost, int socketPort, String hashSalt, DatabaseConfig database, PanelConfig panel) {
    private static Config config;

    public static Config getSettings() {
        if (config == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            GsonFileLoader<Config> settingsLoader = new GsonFileLoader<>(FileManager.getConfigFile(), gson, Config.class);
            if (!FileManager.getConfigFile().isFile()) {
                settingsLoader.save(new Config(
                        "0.0.0.0",
                        27767,
                        "CHANGE ME!",
                        new DatabaseConfig(
                                "user",
                                "password",
                                "0.0.0.0",
                                27017
                        ),
                        new PanelConfig(
                                "https://panel.atlasworld.fr",
                                "token",
                                1,
                                new PanelConfig.Defaults("example", "example")
                        )));
            }

            config = settingsLoader.load();
        }
        return config;
    }
}
