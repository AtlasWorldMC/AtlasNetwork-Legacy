package fr.atlasworld.network.database.mongo;

import com.mongodb.MongoException;
import com.mongodb.MongoSocketClosedException;
import com.mongodb.MongoSocketReadException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.config.DatabaseConfig;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseConnectionClosedException;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.database.DatabaseIOException;
import fr.atlasworld.network.exceptions.database.DatabaseTimeoutException;
import fr.atlasworld.network.panel.ServerInfo;

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
    public Database<AuthProfile> getAuthenticationProfileDatabase() throws DatabaseException {
        try {
            return new MongoDatabase<>(
                    AuthProfile.class,
                    this.client.getDatabase(INTERNAL_DATABASE).getCollection(AUTH_PROFILE_COLLECTION),
                    id -> Filters.eq("profileId", id),
                    this.serializer
            );
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not retrieve data from database");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of data stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending request");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public Database<ServerInfo> getServerDatabase() throws DatabaseException {
        try {
            return new MongoDatabase<>(
                    ServerInfo.class,
                    this.client.getDatabase(INTERNAL_DATABASE).getCollection(SERVER_COLLECTION),
                    id -> Filters.eq("id", id),
                    this.serializer
            );
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not retrieve data from database");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of data stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending request");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void close() {
        this.client.close();
    }
}
