package fr.atlasworld.network.networking.session;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ClientSession {
    private final SessionManager sessionManager;
    private final Channel channel;
    private boolean authenticated;

    public ClientSession(SessionManager sessionManager, Channel channel) {
        this.sessionManager = sessionManager;
        this.channel = channel;
        this.authenticated = false;
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

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void authenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
