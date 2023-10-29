package fr.atlasworld.network.services.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.atlasworld.network.config.files.DatabaseConfiguration;
import fr.atlasworld.network.services.database.Database;
import fr.atlasworld.network.services.database.DatabaseEntityFactory;
import fr.atlasworld.network.services.database.entities.DatabaseEntity;
import fr.atlasworld.network.services.database.DatabaseService;
import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import fr.atlasworld.network.services.database.exceptions.DatabaseIOException;
import fr.atlasworld.network.services.database.exceptions.DatabaseTimeoutException;
import fr.atlasworld.network.services.database.exceptions.unchecked.DatabaseConnectionClosedException;
import fr.atlasworld.network.services.database.mongo.listeners.CommandLogger;
import fr.atlasworld.network.services.database.mongo.listeners.HeartbeatMonitorListener;

import java.util.concurrent.TimeUnit;

public class MongoDatabaseService implements DatabaseService {
    public static final String SERVER_DATABASE = "server";

    private final MongoClient client;
    private boolean closed;

    public MongoDatabaseService(MongoClient client) {
        this.client = client;
        this.closed = false;
    }

    public MongoDatabaseService(DatabaseConfiguration configuration) {
        this(MongoClients.create(MongoClientSettings.builder()
                .credential(configuration.credentials().getCredentials())
                .applicationName(configuration.applicationName())
                .retryWrites(configuration.retryReadsWrites())
                .retryReads(configuration.retryReadsWrites())
                .addCommandListener(new CommandLogger())
                .applyToClusterSettings(builder ->
                        builder.hosts(configuration.clusterSettings().hosts())
                                .mode(configuration.clusterSettings().getMode())
                                .serverSelectionTimeout(configuration.clusterSettings().serverSelectionTimeout(), TimeUnit.SECONDS)
                )
                .applyToConnectionPoolSettings(builder ->
                        builder.maxConnectionIdleTime(configuration.connectionPoolSettings().maxIdleTime(), TimeUnit.SECONDS)
                                .maxConnectionLifeTime(configuration.connectionPoolSettings().maxLifeTime(), TimeUnit.SECONDS)
                                .maxWaitTime(configuration.connectionPoolSettings().maxWaitTime(), TimeUnit.SECONDS)
                )
                .applyToServerSettings(builder ->
                        builder.heartbeatFrequency(configuration.serverSettings().heartBeatFrequency(), TimeUnit.SECONDS)
                                .addServerMonitorListener(new HeartbeatMonitorListener(configuration.serverSettings().heartBeatFrequency())))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(configuration.socketSettings().connectTimeout(), TimeUnit.SECONDS)
                                .readTimeout(configuration.socketSettings().readTimeout(), TimeUnit.SECONDS)
                ).build()
        ));
    }

    @Override
    public <T extends DatabaseEntity<T>> Database<T> getDatabase(String name, DatabaseEntityFactory<T> factory) throws DatabaseException {
        this.ensureNotClosed();
        try {
            return new MongoDatabase<>(this.client.getDatabase(SERVER_DATABASE).getCollection(name), factory);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Database request timed-out.");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of data stream.");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending request.");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void closeConnection() throws DatabaseException {
        try {
            this.closed = true;
            this.client.close();
        } catch (Throwable throwable) {
            throw new DatabaseException("Failed to close connection.", throwable);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }
}
