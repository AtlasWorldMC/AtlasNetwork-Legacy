package fr.atlasworld.network.config.files;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import fr.atlasworld.network.config.IConfiguration;
import fr.atlasworld.network.config.IConfigurationSchema;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import org.jetbrains.annotations.NotNull;

public record SocketConfiguration(String host, int port, @SerializedName("restart_when_closed") boolean restartWhenClosed) implements IConfiguration {
    public static class SocketConfigurationSchema implements IConfigurationSchema<SocketConfiguration> {

        @Override
        public @NotNull String filename() {
            return "socket.json";
        }

        @Override
        public @NotNull Class<SocketConfiguration> configurationClass() {
            return SocketConfiguration.class;
        }

        @Override
        public @NotNull SocketConfiguration defaultConfiguration() {
            return new SocketConfiguration("0.0.0.0", 27767, true);
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
}
