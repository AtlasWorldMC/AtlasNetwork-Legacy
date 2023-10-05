package fr.atlasworld.network.networking.socket;

import fr.atlasworld.network.api.networking.NetworkManager;
import fr.atlasworld.network.networking.SocketManager;
import fr.atlasworld.network.networking.handler.*;
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
    private final int port;
    private final String address;

    private boolean bound;
    private Channel serverChannel;

    public NetworkSocketManager(SecurityManager securityManager, int port, String address) {
        this.securityManager = securityManager;
        this.port = port;
        this.address = address;
        this.bound = false;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    public NetworkSocketManager(InetSocketAddress address, SecurityManager securityManager) {
        this(securityManager, address.getPort(), address.getHostName());
    }

    @Override
    public boolean isRunning() {
        return this.bound;
    }

    @Override
    public InetSocketAddress getBoundAddress() {
        return new InetSocketAddress(this.address, this.port);
    }

    @Override
    public NetworkManager getNetworkManager() {
        return null;
    }

    @Override
    public void stop() {
        if (!this.bound) {
            throw new UnsupportedOperationException("Socket can only be stopped when running!");
        }

        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();

        this.serverChannel.disconnect();
        this.serverChannel = null;
    }

    @Override
    public ChannelFuture start() {
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
                        ch.pipeline().addLast(new ExceptionHandler());

                        //Out
                        ch.pipeline().addFirst(new EncodeHandler(encryptionManager));
                    }
                });

        return boot;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public InetSocketAddress localAddress() {
        return null;
    }
}
