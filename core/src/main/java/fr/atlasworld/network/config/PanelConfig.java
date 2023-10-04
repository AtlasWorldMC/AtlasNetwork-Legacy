package fr.atlasworld.network.config;

/**
 * Panel Configuration file
 * @param url panel url
 * @param token panel api token
 * @param userId account id
 * @param defaults default configurations
 */

@Deprecated
public record PanelConfig(String url, String token, long userId, Defaults defaults) {

    /**
     * Default server & proxy configuration
     * @param proxy default proxy server configuration
     * @param server default server server configuration
     */
    public record Defaults(String proxy, String server) {
    }
}
