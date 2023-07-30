package fr.atlasworld.network.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;

import java.util.List;

public class Settings {
    private final String socketHost;
    private final int socketPort;
    private final DatabaseSettings database;

    public Settings(String socketHost, int socketPort, DatabaseSettings database) {
        this.socketHost = socketHost;
        this.socketPort = socketPort;
        this.database = database;
    }

    public String getSocketHost() {
        return socketHost;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public DatabaseSettings getDatabase() {
        return database;
    }

    //Static fields
    private static Settings settings;

    public static Settings getSettings() {
        if (settings == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            GsonFileLoader<Settings> settingsLoader = new GsonFileLoader<>(FileManager.getSettingsFile(), gson, Settings.class);
            if (!FileManager.getSettingsFile().isFile()) {
                settingsLoader.save(new Settings("0.0.0.0", 27767,
                        new DatabaseSettings("user", "password", "0.0.0.0",
                                27017)));
            }

            settings = settingsLoader.load();
        }
        return settings;
    }

    public record DatabaseSettings(String username, String password, String host, int port) {
    }
}
