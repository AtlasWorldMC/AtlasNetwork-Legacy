package fr.atlasworld.network.database.mongo;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.config.DatabaseConfig;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.Server;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseEntryNotFoundException;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.database.DatabaseEntryImmutableException;
import fr.atlasworld.network.exceptions.database.DatabaseTimeoutException;
import fr.atlasworld.network.integration.ptero.PteroServer;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MongoDatabaseManager implements DatabaseManager {
    private static final String INTERNAL_DATABASE = "internal";
    private static final String AUTH_PROFILE_COLLECTION = "profiles";
    private static final String SERVER_COLLECTION = "servers";

    private final MongoClient client;
    private final MongoDatabaseSerializer serializer;

    public MongoDatabaseManager(DatabaseConfig config, MongoDatabaseSerializer serializer) {
        String connectionString =
                "mongodb://" + config.username() + ":" + config.password() + "@" +
                        config.host() + ":" + config.port() + "/";

        this.client = MongoClients.create(connectionString);
        this.serializer = serializer;
    }

    public MongoDatabaseManager(DatabaseConfig config) {
        this(config, new MongoDatabaseSerializer());
    }

    @Override
    public @Nullable AuthProfile getAuthProfile(UUID uuid) throws DatabaseException {
        MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
        MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

        Document docProfile = profileCollection.find(Filters.eq("profileId", uuid.toString())).first();
        if (docProfile == null) {
            return null;
        }

        return this.serializer.deserialize(docProfile, AuthProfile.class);
    }

    @Override
    public Set<AuthProfile> getAuthProfiles() throws DatabaseException {
        try {
            MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
            MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

            Set<AuthProfile> profiles = new HashSet<>();
            for (Document doc : profileCollection.find()) {
                profiles.add(this.serializer.deserialize(doc, AuthProfile.class));
            }

            return profiles;
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not retrieve auth profiles");
        }
    }

    @Override
    public boolean authProfileExists(UUID uuid) throws DatabaseException {
        return this.getAuthProfile(uuid) != null;
    }

    @Override
    public void saveAuthProfile(AuthProfile profile) throws DatabaseException {
        try {
            MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
            MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

            if (authProfileExists(profile.profileId())) {
                throw new DatabaseEntryImmutableException("Auth Profile cannot be modified/replaced!");
            }

            profileCollection.insertOne(this.serializer.serialize(profile));
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not save auth profile");
        }
    }

    @Override
    public void deleteAuthProfile(UUID uuid) throws DatabaseException {
        try {
            MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
            MongoCollection<Document> profileCollection = database.getCollection(AUTH_PROFILE_COLLECTION);

            if (!authProfileExists(uuid)) {
                throw new DatabaseEntryNotFoundException("Profile does not exists!");
            }

            profileCollection.deleteOne(Filters.eq("profileId", uuid.toString()));
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not delete auth profile");
        }
    }

    @Override
    public @Nullable Server getServer(UUID serverId) throws DatabaseException {
        try {
            MongoDatabase database = this.client.getDatabase(INTERNAL_DATABASE);
            MongoCollection<Document> serverCollection = database.getCollection(SERVER_COLLECTION);

            Document serverDoc = serverCollection.find(Filters.eq("id", serverId.toString())).first();
            if (serverDoc == null) {
                return null;
            }

            return this.serializer.deserialize(serverDoc, Server.class);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not retrieve server");
        }
    }

    @Override
    public boolean serverExists(UUID serverId) throws DatabaseException {
        return this.getServer(serverId) != null;
    }

    @Override
    public void saveServer(PteroServer server) throws DatabaseException {

    }

    @Override
    public void deleteServer(UUID uuid) {

    }

    @Override
    public void close() {
        this.client.close();
    }
}
