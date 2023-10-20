package fr.atlasworld.network.networking.entities;

import fr.atlasworld.network.INetworkEntity;
import io.netty.channel.ChannelFuture;

/**
 * Represents a network session
 */
public interface INetworkSession extends INetworkEntity {
    /**
     * Retrieve the channel's close future
     */
    ChannelFuture closeFuture();
}
