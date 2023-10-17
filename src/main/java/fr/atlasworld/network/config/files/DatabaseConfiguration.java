package fr.atlasworld.network.config.files;

import com.google.gson.JsonElement;
import fr.atlasworld.network.config.IConfiguration;
import fr.atlasworld.network.config.IConfigurationSchema;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import org.jetbrains.annotations.NotNull;

public record DatabaseConfiguration(String host, int port, String username, String password) implements IConfiguration {
    public static class DatabaseConfigurationSchema implements IConfigurationSchema<DatabaseConfiguration> {
        @Override
        public @NotNull String filename() {
            return "database.json";
        }

        @Override
        public @NotNull Class<DatabaseConfiguration> configurationClass() {
            return DatabaseConfiguration.class;
        }

        @Override
        public @NotNull DatabaseConfiguration defaultConfiguration() {
            return new DatabaseConfiguration("127.0.0.1", 27017, "username", "password");
        }

        @Override
        public int configurationVersion() {
            return 1;
        }

        @Override
        public @NotNull JsonElement updateConfiguration(JsonElement json, int version) {
            throw new UnsupportedConfigurationVersionException("Only version 1 is supported.");
        }
    }
}
