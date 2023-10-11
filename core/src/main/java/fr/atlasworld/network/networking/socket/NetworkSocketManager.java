package fr.atlasworld.network.networking.socket;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.networking.NetworkManager;
import fr.atlasworld.network.networking.SocketManager;
import fr.atlasworld.network.networking.handler.*;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.security.authentication.AuthenticationManager;
import fr.atlasworld.network.networking.security.authentication.NetworkAuthenticationManager;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import fr.atlasworld.network.networking.security.encryption.NetworkEncryptionManager;
import fr.atlasworld.network.networking.session.NetworkSessionManager;
import fr.atlasworld.network.networking.session.SessionManager;
import fr.atlasworld.network.networking.security.SecurityManager;
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
    private final int port;
    private final String address;

    private boolean bound = false;
    private Channel serverChannel;

    public NetworkSocketManager(SecurityManager securityManager, PacketManager packetManager, int port, String address) {
        this.securityManager = securityManager;
        this.packetManager = packetManager;
        this.port = port;
        this.address = address;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    public NetworkSocketManager(InetSocketAddress address, SecurityManager securityManager, PacketManager packetManager) {
        this(securityManager, packetManager, address.getPort(), address.getHostName());
    }

    @Override
    public synchronized boolean isRunning() {
        return this.bound;
    }

    @Override
    public synchronized InetSocketAddress getBoundAddress() {
        return new InetSocketAddress(this.address, this.port);
    }

    @Override
    public synchronized NetworkManager getNetworkManager() {
        return null;
    }

    @Override
    public synchronized void stop() {
        if (!this.bound) {
            throw new UnsupportedOperationException("Socket can only be stopped when running!");
        }

        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();

        this.serverChannel.disconnect();
        this.serverChannel = null;
    }

    @Override
    public synchronized ChannelFuture start() {
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

        return boot.bind(this.address, this.port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                this.bound = true;
                future.channel().closeFuture().addListener(closeFuture -> {
                    this.bound = false;
                });
            } else {
                AtlasNetwork.logger.error("Could not start socket!", future.cause());
            }
        });
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public InetSocketAddress localAddress() {
        return new InetSocketAddress(this.address, this.port);
    }

    @Override
    public PacketManager getPacketManager() {
        return this.packetManager;
    }
}
