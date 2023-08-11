package fr.atlasworld.network.database.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.AuthProfileOverrideException;
import fr.atlasworld.network.utils.Settings;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MongoDatabaseManager implements DatabaseManager {
    private static final String INTERNAL_DATABASE = "internal";
    private static final String AUTH_PROFILE_COLLECTION = "profiles";

    private final MongoClient client;
    private final MongoDatabaseSerializer serializer;

    public MongoDatabaseManager(Settings.DatabaseSettings settings, MongoDatabaseSerializer serializer) {
        String connectionString =
                "mongodb://" + settings.username() + ":" + settings.password() + "@" +
                        settings.host() + ":" + settings.port() + "/";

        this.client = MongoClients.create(connectionString);
        this.serializer = serializer;
    }

    public MongoDatabaseManager(Settings.DatabaseSettings settings) {
        this(settings, new MongoDatabaseSerializer());
    }

    @Override
    public @Nullable AuthProfile getAuthProfile(UUID uuid) {
        MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
        MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

        Document docProfile = profileCollection.find(Filters.eq("profileId", uuid.toString())).first();
        if (docProfile == null) {
            return null;
        }

        return this.serializer.deserialize(docProfile, AuthProfile.class);
    }

    @Override
    public Set<AuthProfile> getAuthProfiles() {
        MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
        MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

        Set<AuthProfile> profiles = new HashSet<>();
        for (Document doc : profileCollection.find()) {
            profiles.add(this.serializer.deserialize(doc, AuthProfile.class));
        }

        return profiles;
    }

    @Override
    public boolean authProfileExists(UUID uuid) {
        return this.getAuthProfile(uuid) != null;
    }

    @Override
    public void saveAuthProfile(AuthProfile profile) {
        MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
        MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

        if (authProfileExists(profile.profileId())) {
            throw new AuthProfileOverrideException("Auth Profile cannot be modified/replaced!");
        }

        profileCollection.insertOne(this.serializer.serialize(profile));
    }

    @Override
    public void deleteAuthProfile(UUID uuid) {
        MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
        MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

        if (!authProfileExists(uuid)) {
            throw new AuthProfileOverrideException("Profile doesn't exists!");
        }

        profileCollection.deleteOne(Filters.eq("profileId", uuid.toString()));
    }
}
