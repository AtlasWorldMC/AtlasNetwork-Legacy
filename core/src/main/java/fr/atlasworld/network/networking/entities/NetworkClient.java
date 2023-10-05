package fr.atlasworld.network.networking.entities;

import fr.atlasworld.network.api.networking.NetworkSource;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;
import fr.atlasworld.network.networking.packet.SentPacketImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * Network client, 'holder' for managing socket channels more easily using Network's Packet based API
 */
public class NetworkClient implements NetworkSource {
    private final UUID id;
    private final Channel channel;

    public NetworkClient(UUID id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public CompletableFuture<SentPacket> sendPacket(PacketByteBuf buf) {
        CompletableFuture<SentPacket> future = new CompletableFuture<>();

        this.channel.writeAndFlush(buf).addListener((ChannelFuture cFuture) -> {
            if (cFuture.isSuccess()) {
                future.complete(new SentPacketImpl(buf, this));
            } else {
                future.completeExceptionally(cFuture.cause());
            }
        });

        return future;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public boolean disconnect(String reason) {
        return this.channel.disconnect().syncUninterruptibly().isSuccess();
    }
}
