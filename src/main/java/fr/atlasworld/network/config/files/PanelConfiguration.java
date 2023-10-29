package fr.atlasworld.network.config.files;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import fr.atlasworld.network.config.Configuration;
import fr.atlasworld.network.config.ConfigurationSchema;
import fr.atlasworld.network.config.exceptions.unchecked.UnsupportedConfigurationVersionException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PanelConfiguration(String url, String token, @SerializedName("account_id") int accountId,
                                 @SerializedName("default_servers") List<String> defaultServers,
                                 @SerializedName("haproxy_servers") List<String> haproxyServers,
                                 @SerializedName("node_blacklist") List<Long> nodeBlacklist) implements Configuration {
    public static class PanelConfigurationSchema implements ConfigurationSchema<PanelConfiguration> {
        @Override
        public @NotNull String filename() {
            return "panel.json";
        }

        @Override
        public @NotNull Class<PanelConfiguration> configurationClass() {
            return PanelConfiguration.class;
        }

        @Override
        public @NotNull PanelConfiguration defaultConfiguration() {
            return new PanelConfiguration("https://panel.atlasworld.fr", "token", 0, ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
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
