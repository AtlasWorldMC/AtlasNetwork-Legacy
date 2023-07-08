package fr.atlasworld.network.networking;

import fr.atlasworld.network.entities.internal.InternalUser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Client {
    private final Channel channel;
    private final InternalUser user;

    public Client(Channel channel, InternalUser user) {
        this.channel = channel;
        this.user = user;
    }

    public ChannelFuture sendPacket(PacketByteBuf buf) {
        return this.channel.writeAndFlush(buf.getParent());
    }

    public ChannelFuture disconnect() {
        return this.channel.disconnect();
    }

    public Channel getChannel() {
        return channel;
    }

    public InternalUser getUser() {
        return this.user;
    }
}
