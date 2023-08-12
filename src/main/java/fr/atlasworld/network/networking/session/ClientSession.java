package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

public class ClientSession {
    private final SessionManager sessionManager;
    private final Channel channel;

    public ClientSession(SessionManager sessionManager, Channel channel) {
        this.sessionManager = sessionManager;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public ChannelFuture sendPacket(PacketByteBuf buf) {
        return this.channel.writeAndFlush(buf);
    }
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }
}
