package fr.atlasworld.network.config.files;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import fr.atlasworld.network.config.Configuration;
import fr.atlasworld.network.config.ConfigurationSchema;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import fr.atlasworld.network.security.SecurityUtilities;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public record SecurityConfiguration(@SerializedName("hashing_salt") String hashSalt) implements Configuration {
    public static class SecurityConfigurationSchema implements ConfigurationSchema<SecurityConfiguration> {

        @Override
        public @NotNull String filename() {
            return "security.json";
        }

        @Override
        public @NotNull Class<SecurityConfiguration> configurationClass() {
            return SecurityConfiguration.class;
        }

        @Override
        public @NotNull SecurityConfiguration defaultConfiguration() {
            return new SecurityConfiguration(Base64.getEncoder().encodeToString(SecurityUtilities.getNextSalt()));
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
