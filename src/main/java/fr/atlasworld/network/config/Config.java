package fr.atlasworld.network.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;

/**
 * Base Configuration file
 * @param socketHost bind host
 * @param socketPort bind port
 * @param hashSalt salt used for hashing
 * @param database database configuration
 * @param panel panel configuration
 * @param balancer balancer configuration
 *
 * @see DatabaseConfig
 * @see PanelConfig
 * @see BalancerConfig
 */
public record Config(String socketHost, int socketPort, @SerializedName("hash_salt") String hashSalt, DatabaseConfig database, PanelConfig panel, BalancerConfig balancer) {
    private static Config config;

    public static Config getSettings() {
        if (config == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            GsonFileLoader<Config> configLoader = new GsonFileLoader<>(FileManager.getConfigFile(), gson, Config.class);
            if (!configLoader.fileExists()) {
                configLoader.save(new Config(
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
                        ),
                        new BalancerConfig(
                                "https://balance-api.atlasworld.fr",
                                "minecraft",
                                "username",
                                "password"
                        )));
            }

            config = configLoader.load();
        }
        return config;
    }
}
