package fr.atlasworld.network.old_networking;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.old_networking.auth.TokenAuthenticationManager;
import fr.atlasworld.network.old_networking.session.SessionManager;
import fr.atlasworld.network.old_networking.handler.AuthHandler;
import fr.atlasworld.network.old_networking.handler.EventHandler;
import fr.atlasworld.network.old_networking.handler.PacketExecutor;
import fr.atlasworld.network.old_networking.packet.PacketManager;
import fr.atlasworld.network.utils.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jetbrains.annotations.NotNull;

public class OldSocketManager {
    private boolean connected = false;
    private final ServerBootstrap bootstrap;
    private final int port;
    private final String host;
    private final NioEventLoopGroup bossGroup, workerGroup;
    private Channel serverChannel;

    private OldSocketManager(Settings settings) {
        this.bootstrap = new ServerBootstrap();
        this.port = settings.getSocketPort();
        this.host = settings.getSocketHost();

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        //Setup Bootstrap
        this.bootstrap
                .channel(NioServerSocketChannel.class)
                .group(bossGroup, workerGroup)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NotNull SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new AuthHandler(TokenAuthenticationManager.getManager(), SessionManager.getManager()));
                        ch.pipeline().addLast(new PacketExecutor(PacketManager.getManager(), SessionManager.getManager()));
                        ch.pipeline().addLast(new EventHandler());
                    }
                });
    }

    public ChannelFuture bind() {
        return this.bootstrap
                .bind(this.host, this.port)
                .addListener((ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        this.connected = false;
                        AtlasNetwork.logger.error("Could not bind to {}:{}", this.host, this.port, future.cause());
                        return;
                    }

                    AtlasNetwork.logger.info("Successfully bound to {}:{}", this.host, this.port);

                    this.connected = true;
                    this.serverChannel = future.channel();

                    this.serverChannel.closeFuture().addListener(closeFuture -> {
                        this.connected = false;
                        this.serverChannel = null;
                        AtlasNetwork.logger.warn("Socket stopped!");
                    });
                });
    }

    public void unbind() {
        if (!this.connected) {
            AtlasNetwork.logger.warn("Unbind method triggered while socket is not connected!");
            return;
        }

        try {
            this.serverChannel.close().sync();
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            AtlasNetwork.logger.error("Unable to close socket", e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }


    //Static fields
    private static OldSocketManager manager;

    public static OldSocketManager getManager() {
        if (manager == null) {
            manager = new OldSocketManager(Settings.getSettings());
        }
        return manager;
    }
}
