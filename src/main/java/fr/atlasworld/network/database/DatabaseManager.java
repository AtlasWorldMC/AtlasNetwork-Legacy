package fr.atlasworld.network.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.atlasworld.network.database.serializers.DatabaseSerializer;
import fr.atlasworld.network.utils.Settings;
import org.bson.Document;

public class DatabaseManager {
    private final MongoClient client;

    private DatabaseManager(Settings.DatabaseSettings settings) {
        String connectionString =
                "mongodb://" + settings.username() + ":" + settings.password() + "@" +
                        settings.host() + ":" + settings.port() + "/";

        this.client = MongoClients.create(connectionString);
    }

    public MongoClient getClient() {
        return client;
    }
    public <T> T deserialize(DatabaseSerializer<T> serializer, Document document) {
        return serializer.deserialize(document);
    }

    public <T> Document serialize(DatabaseSerializer<T> serializer, T object) {
        return serializer.serialize(object);
    }

    //Static Fields
    private static DatabaseManager manager;

    public static DatabaseManager getManager() {
        if (manager == null) {
            manager = new DatabaseManager(Settings.getSettings().getDatabase());
        }

        return manager;
    }
}
