package fr.atlasworld.network.entities.auth;

import java.util.UUID;

public record AuthProfile(UUID profileId, String tokenHash) {
}
