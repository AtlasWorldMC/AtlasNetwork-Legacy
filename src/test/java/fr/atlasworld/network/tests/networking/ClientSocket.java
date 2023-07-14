package fr.atlasworld.network.tests.networking;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.tests.NetworkClient;
import fr.atlasworld.network.tests.networking.handler.SimpleHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientSocket {
    private boolean connected = false;
    private final Bootstrap bootstrap;
    private final int port;
    private final String host;
    private final NioEventLoopGroup workerGroup;
    private Channel channel;

    public ClientSocket(int port, String host) {
        this.bootstrap = new Bootstrap();
        this.port = port;
        this.host = host;

        this.workerGroup = new NioEventLoopGroup();

        //Setup Bootstrap
        this.bootstrap
                .channel(NioSocketChannel.class)
                .group(workerGroup)
                .handler(new SimpleHandler());
    }

    public ChannelFuture connect() {
        return this.bootstrap
                .connect(this.host, this.port)
                .addListener((ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        this.connected = false;
                        NetworkClient.logger.error("Could not connect to {}:{}", this.host, this.port, future.cause());
                        return;
                    }

                    NetworkClient.logger.info("Successfully connected to {}:{}", this.host, this.port);

                    this.connected = true;
                    this.channel = future.channel();

                    this.channel.closeFuture().addListener(closeFuture -> {
                        this.connected = false;
                        this.channel = null;
                        NetworkClient.logger.warn("Socket stopped!");
                    });
                });
    }

    public void unbind() {
        if (!this.connected) {
            NetworkClient.logger.warn("Unbind method triggered while socket is not connected!");
            return;
        }

        try {
            this.channel.close().sync();
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

    public Channel getChannel() {
        return channel;
    }
}
