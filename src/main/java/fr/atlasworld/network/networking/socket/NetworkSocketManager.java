package fr.atlasworld.network.networking.socket;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.boot.NetworkBootstrap;
import fr.atlasworld.network.config.files.SocketConfiguration;
import fr.atlasworld.network.networking.handler.*;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.security.authentication.NetworkAuthenticationManager;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import fr.atlasworld.network.networking.security.encryption.NetworkEncryptionManager;
import fr.atlasworld.network.networking.session.NetworkSessionManager;
import fr.atlasworld.network.networking.session.SessionManager;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

/**
 * Network implementation of the SocketManager
 * @see SocketManager
 */
public class NetworkSocketManager implements SocketManager {
    private final EventLoopGroup bossGroup, workerGroup;
    private final SecurityManager securityManager;
    private final PacketManager packetManager;
    private final SocketConfiguration configuration;

    private Channel serverChannel;
    private boolean bound;

    public NetworkSocketManager(EventLoopGroup bossGroup, EventLoopGroup workerGroup, SecurityManager securityManager, PacketManager packetManager, SocketConfiguration configuration) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.securityManager = securityManager;
        this.packetManager = packetManager;
        this.configuration = configuration;
    }

    public NetworkSocketManager(SocketConfiguration config, SecurityManager securityManager, PacketManager packetManager) {
        this(new NioEventLoopGroup(), new NioEventLoopGroup(), securityManager, packetManager, config);
    }

    @Override
    public synchronized ChannelFuture start() {
        if (this.bound) {
            throw new UnsupportedOperationException("Socket cannot be started multiple times!");
        }

        ServerBootstrap boot = new ServerBootstrap();
        boot.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NotNull SocketChannel ch) throws Exception {
                        EncryptionManager encryptionManager = new NetworkEncryptionManager(securityManager);
                        AuthenticationManager authHandler = new NetworkAuthenticationManager(securityManager, null);
                        SessionManager sessionManager = new NetworkSessionManager();

                        //In
                        ch.pipeline().addLast(new DecodeHandler(new NetworkEncryptionManager(securityManager)));
                        ch.pipeline().addLast(new AuthenticationHandler(authHandler, sessionManager));
                        ch.pipeline().addLast(new PacketHandler(packetManager, sessionManager));
                        ch.pipeline().addLast(new ResourcesHandler());

                        //Out
                        ch.pipeline().addFirst(new EncodeHandler(encryptionManager));
                    }
                });

        return boot.bind(this.configuration.host(), this.configuration.port()).addListener((ChannelFuture connectionFuture) -> {
            if (!connectionFuture.isSuccess()) {
                AtlasNetwork.logger.error("Could not start socket!");
                NetworkBootstrap.crash(connectionFuture.cause());
                return;
            }

            this.serverChannel = connectionFuture.channel();
            this.serverChannel.closeFuture().addListener(closeFuture -> {
                this.bound = false;

                if (closeFuture.isSuccess()) {
                    return;
                }

                AtlasNetwork.logger.warn("AtlasNetwork's socket has been unexpectedly closed: {}", closeFuture.cause().getMessage());
                AtlasNetwork.logger.debug("Socket has been closed unexpectedly", closeFuture.cause());

                if (!this.configuration.restartWhenClosed()) {
                    NetworkBootstrap.crash(closeFuture.cause());
                    return;
                }

                ChannelFuture restartFuture = this.start().sync();

                if (restartFuture.isSuccess()) {
                    AtlasNetwork.logger.info("Socket restarted.");
                    return;
                }

                AtlasNetwork.logger.error("Could not restart socket.");
                NetworkBootstrap.crash(restartFuture.cause());
            });

            this.bound = true;
        });
    }

    @Override
    public synchronized void stop() {
        if (!this.bound) {
            throw new UnsupportedOperationException("Socket can only be stopped when running!");
        }

        this.serverChannel.disconnect().syncUninterruptibly();

        this.bossGroup.shutdownGracefully().syncUninterruptibly();
        this.workerGroup.shutdownGracefully().syncUninterruptibly();

        this.serverChannel = null;
    }

    @Override
    public synchronized boolean isRunning() {
        return this.bound;
    }

    @Override
    public int getPort() {
        return this.configuration.port();
    }

    @Override
    public String getAddress() {
        return this.configuration.host();
    }

    @Override
    public InetSocketAddress getFullAddress() {
        return new InetSocketAddress(this.getAddress(), this.getPort());
    }

    @Override
    public PacketManager getPacketManager() {
        return this.packetManager;
    }
}
