package fr.atlasworld.network.config;

import com.google.gson.annotations.SerializedName;

/**
 * Load Balancer configuration file
 * @param url load balancer api url
 * @param backendName load balancer backend
 * @param username load balancer username
 * @param password load balancer password
 */
@Deprecated
public record BalancerConfig(String url, @SerializedName("backend_name") String backendName, String username, String password) {
}
