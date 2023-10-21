package fr.atlasworld.network.networking.entities.data;

import fr.atlasworld.network.INetworkEntity;

import java.util.UUID;

public record AuthenticationProfile(UUID id, String tokenHash) implements INetworkEntity {
}
