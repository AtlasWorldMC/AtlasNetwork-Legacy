package fr.atlasworld.network.config;

import com.google.gson.*;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.exceptions.ConfigurationException;
import fr.atlasworld.network.config.exceptions.ConfigurationLoadingException;
import fr.atlasworld.network.config.exceptions.ConfigurationParsingException;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.StringFileLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
    private final Map<String, IConfigurationSchema<? extends Configuration>> registeredConfigurationSchemas;
    private final Map<String, Configuration> loadedConfigurations;
    private final Gson gson;

    private boolean loaded = false;

    public ConfigurationManager(Map<String, IConfigurationSchema<? extends Configuration>> registeredConfigurationSchemas, Map<String, Configuration> loadedConfigurations, Gson gson) {
        this.registeredConfigurationSchemas = registeredConfigurationSchemas;
        this.loadedConfigurations = loadedConfigurations;
        this.gson = gson;
    }

    public ConfigurationManager() {
        this(new HashMap<>(), new HashMap<>(), new GsonBuilder().setPrettyPrinting().create());
    }

    public void registerSchema(@NotNull IConfigurationSchema<? extends Configuration> schema) {
        if (this.loaded) {
            throw new UnsupportedOperationException("Configuration schemas must be registered before loading configuration.");
        }

        if (this.registeredConfigurationSchemas.containsKey(schema.filename())) {
            throw new UnsupportedOperationException("Configuration schemas can only be registered once.");
        }

        this.registeredConfigurationSchemas.put(schema.filename(), schema);
    }

    public void loadConfigurations() throws ConfigurationException {
        if (this.loaded) {
            throw new UnsupportedOperationException("Configuration files can only be loaded once!");
        }

        File configurationDirectory = FileManager.getConfigDirectory();

        if (!configurationDirectory.isDirectory()) {
            AtlasNetwork.logger.info("Generating configuration files..");
            configurationDirectory.mkdirs();
        }

        for (IConfigurationSchema<?> schema : this.registeredConfigurationSchemas.values()) {
            this.loadConfiguration(configurationDirectory, schema);
        }

        this.loaded = true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Configuration> T getConfiguration(String filename, Class<T> configClass) {
        if (!this.loaded) {
            throw new UnsupportedOperationException("Configuration files not yet loaded!");
        }

        Configuration configuration = this.loadedConfigurations.get(filename);
        if (configuration == null) {
            return null;
        }

        try {
            return (T) configuration;
        } catch (ClassCastException e) {
            AtlasNetwork.logger.debug("Failed to cast IConfiguration into '{}'", configClass.getSimpleName());
            return null;
        }
    }

    public @Nullable <T extends Configuration> T getConfiguration(IConfigurationSchema<?> schema, Class<T> configClass) {
        return this.getConfiguration(schema.filename(), configClass);
    }

    private void loadConfiguration(File configurationDirectory, IConfigurationSchema<?> schema) throws ConfigurationLoadingException, ConfigurationParsingException {
        StringFileLoader loader = new StringFileLoader(new File(configurationDirectory, schema.filename()));

        try {
            if (!loader.fileExists()) {
                AtlasNetwork.logger.debug("'{}' configuration file does not exists, creating new file..", schema.filename());
                loader.createFile();

                JsonObject json = new JsonObject();
                json.addProperty("version", schema.configurationVersion());

                Configuration configuration = schema.defaultConfiguration();

                JsonElement configJson = this.gson.toJsonTree(configuration);
                json.add("config", configJson);

                String strJson = this.gson.toJson(json);
                loader.save(strJson);

                this.loadedConfigurations.put(schema.filename(), configuration);
                return;
            }

            JsonObject json = JsonParser.parseString(loader.load()).getAsJsonObject();

            int fileVersion = json.get("version").getAsInt();
            JsonElement configJson = json.get("config");

            // Check Config version
            if (fileVersion != schema.configurationVersion()) {
                configJson = schema.updateConfiguration(configJson, fileVersion);
                loader.save(this.gson.toJson(configJson));
            }

            Configuration configuration = this.gson.fromJson(configJson, schema.configurationClass());

            this.loadedConfigurations.put(schema.filename(), configuration);
        } catch (IOException e) {
            throw new ConfigurationLoadingException("Unable to load configuration file '" + schema.filename() + "':", e);
        } catch (JsonParseException e) {
            throw new ConfigurationParsingException("Unable to parse '" + schema.filename() + "' file is not in valid JSON format:", e);
        } catch (NullPointerException | IllegalStateException e) {
            throw new ConfigurationParsingException("'" + schema.filename() + "' does not match AtlasNetwork configuration structure!");
        } catch (UnsupportedConfigurationVersionException e) {
            throw new ConfigurationParsingException(e);
        }
    }
}

