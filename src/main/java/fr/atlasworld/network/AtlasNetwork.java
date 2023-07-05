package fr.atlasworld.network;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.utils.LaunchArgs;
import fr.atlasworld.network.utils.Settings;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasNetwork {
    public static final Logger logger = LoggerFactory.getLogger("AtlasNetwork");
    public static LaunchArgs launchArgs;

    public static void main(String[] args) {
        AtlasNetwork.logger.info("Initializing AtlasNetwork...");
        //Handle Args
        launchArgs = new LaunchArgs(args);

        //Load configuration
        if (launchArgs.isForceConfig()) {
            AtlasNetwork.logger.warn("AtlasNetwork launched with argument '{}' overriding already existent configuration.", LaunchArgs.FORCE_CONFIG);
            FileManager.getSettingsFile().delete();
        }

    }
}
