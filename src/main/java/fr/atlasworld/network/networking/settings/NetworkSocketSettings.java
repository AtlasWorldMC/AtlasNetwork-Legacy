package fr.atlasworld.network.networking.settings;

import fr.atlasworld.network.networking.handler.DecodeHandler;
import fr.atlasworld.network.networking.handler.EncodeHandler;
import fr.atlasworld.network.networking.handler.SessionChannelHandler;
import fr.atlasworld.network.networking.securty.encryption.EncryptionManager;
import fr.atlasworld.network.networking.session.SessionManager;
import fr.atlasworld.network.utils.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class NetworkSocketSettings implements SocketSettings {
    private final int port;
    private final String address;
    private final EventLoopGroup bossGroup, workerGroup;
    private final SessionManager sessionManager;
    private final Supplier<EncryptionManager> encryptionManagerBuilder;

    public NetworkSocketSettings(Settings settings, SessionManager sessionManager, Supplier<EncryptionManager> encryptionManagerBuilder) {
        this.port = settings.getSocketPort();
        this.address = settings.getSocketHost();
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.sessionManager = sessionManager;
        this.encryptionManagerBuilder = encryptionManagerBuilder;
    }

    @Override
    public @NotNull ServerBootstrap getBootstrap() {
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NotNull SocketChannel ch) throws Exception {
                        EncryptionManager encryptionManager = encryptionManagerBuilder.get();

                        //In
                        ch.pipeline().addFirst(new DecodeHandler(encryptionManager));
                        ch.pipeline().addLast(new SessionChannelHandler(sessionManager));

                        //Out
                        ch.pipeline().addFirst(new EncodeHandler(encryptionManager));
                    }
                });

        return boot;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public @NotNull String getAddress() {
        return this.address;
    }

    @Override
    public @NotNull SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public void cleanUp() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
