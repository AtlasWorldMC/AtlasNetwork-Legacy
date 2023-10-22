package fr.atlasworld.network.services.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.atlasworld.network.INetworkEntity;
import fr.atlasworld.network.config.files.DatabaseConfiguration;
import fr.atlasworld.network.services.database.Database;
import fr.atlasworld.network.services.database.DatabaseService;
import fr.atlasworld.network.services.database.exceptions.DatabaseException;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class MongoDatabaseService implements DatabaseService {
    private final MongoClient client;

    public MongoDatabaseService(MongoClient client) {
        this.client = client;
    }

    public MongoDatabaseService(DatabaseConfiguration configuration) {
        this(MongoClients.create(MongoClientSettings.builder()
                .credential(configuration.credentials().getCredentials())
                .applicationName(configuration.applicationName())
                .retryWrites(configuration.retryReadsWrites())
                .retryReads(configuration.retryReadsWrites())
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
                        builder.heartbeatFrequency(configuration.serverSettings().heartBeatFrequency(), TimeUnit.SECONDS))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(configuration.socketSettings().connectTimeout(), TimeUnit.SECONDS)
                                .readTimeout(configuration.socketSettings().readTimeout(), TimeUnit.SECONDS)
                ).build()
        ));
    }

    @Override
    public <T extends INetworkEntity> Database<T> getDatabase(String name, Type type) throws DatabaseException {
        return null;
    }

    @Override
    public void closeConnection() throws DatabaseException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void ensureNotClosed() {
        DatabaseService.super.ensureNotClosed();
    }
}
