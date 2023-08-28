package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.networking.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the client session
 */
@ChannelHandler.Sharable
public class SessionChannelHandler extends ChannelInboundHandlerAdapter {
    private final SessionManager sessionManager;

    public SessionChannelHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        this.sessionManager.removeSession(ctx.channel());
    }
}
