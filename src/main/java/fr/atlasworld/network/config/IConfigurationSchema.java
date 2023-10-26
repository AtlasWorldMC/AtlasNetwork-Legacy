package fr.atlasworld.network.config;

import com.google.gson.JsonElement;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the properties of the configuration file
 * @param <T> the configuration file
 */
public interface IConfigurationSchema<T extends Configuration> {
    /**
     * Retrieve the FULL configuration file name.
     */
    @NotNull String filename();

    /**
     * Retrieve the class of the configuration.
     */
    @NotNull Class<T> configurationClass();

    /**
     * Retrieves the default configuration, used to create the configuration file if it does not exist.
     */
    @NotNull T defaultConfiguration();

    /**
     * Gets the configuration version
     */
    int configurationVersion();

    /**
     * Updates the configuration, to match the actual version
     * @param json the raw json found in the file
     * @param version the version found in the file
     * @return the updated configuration.
     * @throws UnsupportedConfigurationVersionException if the configuration could not be updated.
     */
    @NotNull JsonElement updateConfiguration(JsonElement json, int version);
}
