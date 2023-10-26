package fr.atlasworld.network.networking.entities;

import fr.atlasworld.network.NetworkEntity;
import io.netty.channel.ChannelFuture;

/**
 * Represents a network session
 */
public interface NetworkSession extends NetworkEntity {
    /**
     * Retrieve the channel's close future
     */
    ChannelFuture closeFuture();
}
