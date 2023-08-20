package fr.atlasworld.network.networking.settings;

import fr.atlasworld.network.config.Config;
import fr.atlasworld.network.networking.handler.*;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import fr.atlasworld.network.networking.session.SessionManager;
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
    private final Supplier<AuthenticationManager> authenticationManagerBuilder;
    private final PacketManager packetManager;

    public NetworkSocketSettings(Config config, SessionManager sessionManager, Supplier<EncryptionManager> encryptionManagerBuilder, Supplier<AuthenticationManager> authenticationManagerBuilder, PacketManager packetManager) {
        this.port = config.getSocketPort();
        this.address = config.getSocketHost();
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.sessionManager = sessionManager;
        this.encryptionManagerBuilder = encryptionManagerBuilder;
        this.packetManager = packetManager;
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
                        AuthenticationManager authenticationManager = authenticationManagerBuilder.get();

                        //In
                        ch.pipeline().addLast(new DecodeHandler(encryptionManager));
                        ch.pipeline().addLast(new AuthenticationHandler(authenticationManager));
                        ch.pipeline().addLast(new SessionChannelHandler(sessionManager));
                        ch.pipeline().addLast(new PacketHandler(packetManager, sessionManager));
                        ch.pipeline().addLast(new ExceptionHandler());

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
