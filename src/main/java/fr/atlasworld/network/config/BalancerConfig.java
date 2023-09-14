package fr.atlasworld.network.config;

import com.google.gson.annotations.SerializedName;

public record BalancerConfig(String url, @SerializedName("backend_name") String backendName, String username, String password) {
}
