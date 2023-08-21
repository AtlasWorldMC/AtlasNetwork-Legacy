package fr.atlasworld.network.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;

import java.util.List;

public record Config(String socketHost, int socketPort, String hashSalt, DatabaseConfig database, PanelConfig panel) {
    //Static fields
    private static Config config;

    public static Config getSettings() {
        if (config == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            GsonFileLoader<Config> settingsLoader = new GsonFileLoader<>(FileManager.getSettingsFile(), gson, Config.class);
            if (!FileManager.getSettingsFile().isFile()) {
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
                                List.of(new EggConfig(
                                        "example",
                                        1,
                                        1,
                                        "ghcr.io/software-noob/pterodactyl-images:java_17",
                                        new EggConfig.ResourceConfig(
                                                1,
                                                1,
                                                1024,
                                                0,
                                                100,
                                                1024
                                        ),
                                        new JsonObject()
                                )),
                                new PanelConfig.Defaults("example", "example")
                        )));
            }

            config = settingsLoader.load();
        }
        return config;
    }
}
