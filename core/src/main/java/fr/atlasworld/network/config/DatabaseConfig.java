package fr.atlasworld.network.config;

/**
 * Database configuration file
 * @param username database username
 * @param password database password
 * @param host database host
 * @param port database port
 */
public record DatabaseConfig(String username, String password, String host, int port) {
}
