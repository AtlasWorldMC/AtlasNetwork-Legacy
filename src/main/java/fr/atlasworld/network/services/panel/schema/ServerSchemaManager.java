package fr.atlasworld.network.services.panel.schema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.exceptions.ConfigurationException;
import fr.atlasworld.network.config.exceptions.ConfigurationLoadingException;
import fr.atlasworld.network.config.exceptions.ConfigurationParsingException;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;
import fr.atlasworld.network.services.panel.schema.ServerSchema;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerSchemaManager {
    private final Map<String, ServerSchema> loadedSchemas;
    private final Gson gson;
    private final File rootDirectory;

    public ServerSchemaManager(Map<String, ServerSchema> loadedSchemas, Gson gson, File rootDirectory) {
        this.loadedSchemas = loadedSchemas;
        this.gson = gson;
        this.rootDirectory = rootDirectory;
    }

    public ServerSchemaManager() {
        this(new HashMap<>(), new GsonBuilder().setPrettyPrinting().create(), FileManager.getServerSchemaDirectory());
    }

    /**
     * Load/reload schemas from disk.
     */
    public void loadSchemas() throws ConfigurationException {
        AtlasNetwork.logger.info("Loading server schemas..");
        if (!this.loadedSchemas.isEmpty()) {
            AtlasNetwork.logger.info("Clearing old saved schemas..");
            this.loadedSchemas.clear();
        }

        AtlasNetwork.logger.info("Discovering files..");
        File[] schemaFiles = this.rootDirectory.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));

        if (schemaFiles == null || schemaFiles.length < 1) {
            AtlasNetwork.logger.info("No server schema found.");
            try {
                this.generateExampleSchema();
            } catch (IOException e) {
                AtlasNetwork.logger.error("Could not generate example configuration schema, {}", e.getMessage());
            }
            return;
        }

        AtlasNetwork.logger.info("Found {} server schemas", schemaFiles.length);

        for (File file : schemaFiles) {
            ServerSchema schema = this.loadSchema(file);
            if (this.loadedSchemas.containsKey(schema.id())) {
                throw new ConfigurationException("Found multiple schemas for '" + schema.id() + "'!");
            }

            this.loadedSchemas.put(schema.id(), schema);
            if (!file.getName().replace(".json", "").equals(schema.id())) {
                AtlasNetwork.logger.warn("Server Schema '{}' does not match file name, this is not recommended, but won't cause any issues.", schema.id());
            }

            AtlasNetwork.logger.debug("Loaded Schema '{}' from '{}'", schema.id(), file.getName());
        }

        AtlasNetwork.logger.info("Loaded {} schemas!", this.loadedSchemas.size());
    }

    /**
     * Retrieves a schema using its id
     * @return Null if no schema was loaded using this id.
     */
    public ServerSchema getSchema(String id) {
        return this.loadedSchemas.get(id);
    }

    /**
     * Checks if a schema with this specific id was loaded
     */
    public boolean hasSchema(String id) {
        return this.loadedSchemas.containsKey(id);
    }

    /**
     * Generate the example server schema.
     */
    private void generateExampleSchema() throws IOException {
        GsonFileLoader<ServerSchema> loader = new GsonFileLoader<>(new File(this.rootDirectory, "example.json"),
                this.gson, ServerSchema.class);

        if (!loader.fileExists()) {
            loader.createFile();
        }

        loader.save(ServerSchema.EXAMPLE);
        AtlasNetwork.logger.info("Example server schema generated.");
    }

    /**
     * Load a schema from a file.
     */
    private ServerSchema loadSchema(File file) throws ConfigurationException {
        GsonFileLoader<ServerSchema> loader = new GsonFileLoader<>(file, this.gson, ServerSchema.class);

        try {
            return loader.load();
        } catch (JsonSyntaxException e) {
            throw new ConfigurationParsingException("Failed to parse schema '" + file.getName() + "'!", e);
        } catch (IOException e) {
            throw new ConfigurationLoadingException("Failed to load '" + file.getName() + "'!", e);
        }
    }
}
