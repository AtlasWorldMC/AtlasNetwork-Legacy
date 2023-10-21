package fr.atlasworld.network.networking.entities;

import com.mongodb.lang.Nullable;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.INetworkEntity;
import fr.atlasworld.network.networking.packet.DisconnectPacket;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * Network client, 'holder' for managing socket channels more easily using Network's Packet based API
 */
public class NetworkClient implements INetworkEntity, INetworkSource, INetworkSession {
    private final Channel channel;
    private final UUID uuid;

    public NetworkClient(Channel channel, UUID uuid) {
        this.channel = channel;
        this.uuid = uuid;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public ChannelFuture closeFuture() {
        return this.channel.closeFuture();
    }

    @Override
    public ChannelFuture sendPacket(ByteBuf buffer) {
        return this.channel.writeAndFlush(buffer);
    }

    @Override
    public ChannelFuture disconnect(String reason) {
        ByteBuf packet = DisconnectPacket.createPacket(reason, this.channel.alloc());
        this.sendPacket(packet);
        return this.channel.disconnect();
    }

    @Override
    public ChannelFuture terminate() {
        return this.channel.close();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public void onDisconnect(Consumer<INetworkSource> listener) {
        this.channel.closeFuture().addListener(future -> listener.accept(this));
    }

    @Override
    public void attachData(String key, Object data) {
        if (!AttributeKey.exists(key)) {
            AttributeKey.newInstance(key);
        }

        Attribute<Object> attr = this.channel.attr(AttributeKey.valueOf(key));

        if (attr.get() != null) {
            AtlasNetwork.logger.debug("Overriding attribute data of '{}'; [key={}, oldData={}, newData={}]",
                    this,
                    key,
                    attr.get(),
                    data);
        }

        attr.set(data);
    }

    @Override
    @Nullable
    public <T> T getData(String key) {
        if (!AttributeKey.exists(key)) {
            return null;
        }

        Attribute<T> attribute = this.channel.attr(AttributeKey.valueOf(key));
        return attribute.get();
    }

    @Override
    public PacketByteBuf createPacket() {
        return new PacketByteBuf(this.channel.alloc().buffer());
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public String toString() {
        return this.uuid + "(" + this.remoteAddress() + ")";
    }
}
