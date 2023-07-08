package fr.atlasworld.network.networking.auth;

public record AuthResult(boolean successful, String message, String token, String userId) {
}
