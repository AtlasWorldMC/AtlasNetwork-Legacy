package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.networking.session.ClientSession;
import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the client session
 */
public class SessionChannelHandler extends ChannelInboundHandlerAdapter {
    private final SessionManager sessionManager;

    public SessionChannelHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        this.sessionManager.addSession(ctx.channel(), new ClientSession(this.sessionManager, ctx.channel()));
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        this.sessionManager.removeSession(ctx.channel());
    }
}
