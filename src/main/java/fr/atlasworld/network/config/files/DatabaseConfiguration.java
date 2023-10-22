package fr.atlasworld.network.config.files;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.connection.ClusterConnectionMode;
import fr.atlasworld.network.config.IConfiguration;
import fr.atlasworld.network.config.IConfigurationSchema;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import fr.atlasworld.network.services.database.mongo.exceptions.unchecked.UnsupportedMongoDBAuthenticationMechanism;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DatabaseConfiguration(String docs,
                                    @SerializedName("application_name") String applicationName,
                                    @SerializedName("retry_reads_writes") boolean retryReadsWrites,
                                    DatabaseCredentials credentials,
                                    @SerializedName("cluster") DatabaseClusterSettings clusterSettings,
                                    @SerializedName("connection") DatabaseConnectionSettings connectionPoolSettings,
                                    @SerializedName("server") DatabaseServerSettings serverSettings,
                                    @SerializedName("socket") DatabaseSocketSettings socketSettings
                                    ) implements IConfiguration {
    public static class DatabaseConfigurationSchema implements IConfigurationSchema<DatabaseConfiguration> {
        @Override
        public @NotNull String filename() {
            return "database.json";
        }

        @Override
        public @NotNull Class<DatabaseConfiguration> configurationClass() {
            return DatabaseConfiguration.class;
        }

        @Override
        public @NotNull DatabaseConfiguration defaultConfiguration() {
            return new DatabaseConfiguration(
                    "https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/mongoclientsettings/",
                    "AtlasNetwork",
                    true,
                    new DatabaseCredentials(
                            "username",
                            "password",
                            "",
                            "SCRAM-SHA-256"
                    ),
                    new DatabaseClusterSettings(
                            List.of(
                                    new ServerAddress("localhost", 27017)
                            ),
                            "SINGLE",
                            30
                    ),
                    new DatabaseConnectionSettings(
                            0,
                            0,
                            120
                    ),
                    new DatabaseServerSettings(
                            10
                    ),
                    new DatabaseSocketSettings(
                            10,
                            0
                    )
            );
        }

        @Override
        public int configurationVersion() {
            return 1;
        }

        @Override
        public @NotNull JsonElement updateConfiguration(JsonElement json, int version) {
            throw new UnsupportedConfigurationVersionException("Only version 1 is supported.");
        }
    }

    public record DatabaseCredentials(String username,
                                      String password,
                                      @SerializedName("authentication_database") String authDatabase,
                                      @SerializedName("authentication_mechanism") String authenticationMechanism) {

        public MongoCredential getCredentials() {
            return switch (this.authenticationMechanism.toUpperCase()) {
                case "SCRAM-SHA-256" -> MongoCredential.createScramSha256Credential(this.username, this.authDatabase, this.password.toCharArray());
                case "SCRAM-SHA-1" -> MongoCredential.createScramSha1Credential(this.username, this.authDatabase, this.password.toCharArray());
                case "MONGODB-CR" -> throw UnsupportedMongoDBAuthenticationMechanism.forDeprecated("MONGODB-CR");
                default -> throw new UnsupportedMongoDBAuthenticationMechanism("'" + this.authenticationMechanism + "' is not supported, if it should please update or contact one of the developers.");
            };
        }
    }

    public record DatabaseClusterSettings(
            List<ServerAddress> hosts,
            String mode,
            @SerializedName("server_selection_timeout") long serverSelectionTimeout) {

        public ClusterConnectionMode getMode() {
            return ClusterConnectionMode.valueOf(this.mode.toUpperCase());
        }
    }

    public record DatabaseConnectionSettings(
            @SerializedName("max_idle_time") long maxIdleTime,
            @SerializedName("max_life_time") long maxLifeTime,
            @SerializedName("max_wait_time") long maxWaitTime) {}

    public record DatabaseServerSettings(
            long heartBeatFrequency) {}

    public record DatabaseSocketSettings(
            @SerializedName("connect_timeout") int connectTimeout,
            @SerializedName("read_timeout") int readTimeout) {}
}
