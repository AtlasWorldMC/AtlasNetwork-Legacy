package fr.atlasworld.network.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;

import java.util.ArrayList;

public class Config {
    private final String socketHost;
    private final int socketPort;
    private final String hashSalt;
    private final DatabaseConfig database;
    private final PanelConfig panel;

    public Config(String socketHost, int socketPort, String hashSalt, DatabaseConfig database, PanelConfig panel) {
        this.socketHost = socketHost;
        this.socketPort = socketPort;
        this.hashSalt = hashSalt;
        this.database = database;
        this.panel = panel;
    }

    public String getSocketHost() {
        return socketHost;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public String getHashSalt() {
        return hashSalt;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public PanelConfig getPanel() {
        return panel;
    }

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
                                "panel.atlasworld.fr",
                                "<token>",
                                new PanelConfig.EggsConfig(
                                        new ArrayList<>())
                        )));
            }

            config = settingsLoader.load();
        }
        return config;
    }
}
