package fr.atlasworld.network.networking.socket;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.settings.SocketSettings;
import io.netty.channel.ChannelFuture;

public class NetworkSocketManager implements SocketManager {
    private final SocketSettings settings;
    private boolean bound;

    public NetworkSocketManager(SocketSettings settings) {
        this.settings = settings;
    }

    @Override
    public int getPort() {
        return this.settings.getPort();
    }

    @Override
    public String getAddress() {
        return this.settings.getAddress();
    }

    @Override
    public boolean isBound() {
        return this.bound;
    }

    @Override
    public ChannelFuture bind() {
        return this.settings.getBootstrap()
                .bind(this.settings.getAddress(), this.settings.getPort())
                .addListener((ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        this.bound = false;
                        AtlasNetwork.logger.error("[Socket] Could not bind to {}:{}", this.settings.getAddress(),
                                this.settings.getPort(), future.cause());
                        return;
                    }

                    AtlasNetwork.logger.info("[Socket] Bound to {}:{}", this.settings.getAddress(), this.settings.getPort());

                    this.bound = true;

                    future.channel().closeFuture().addListener(closeFuture -> {
                        this.bound = false;
                        this.settings.cleanUp();
                        AtlasNetwork.logger.error("[Socket] Disconnected!");
                        AtlasNetwork.logger.warn("If this is caused by a connection issue you can ignore this message");
                        AtlasNetwork.logger.warn("Please stop the socket by using the SocketManager#unbind() method!");
                    });
                });
    }

    @Override
    public void unbind() {
        this.settings.cleanUp();
        AtlasNetwork.logger.info("[Socket] Disconnected!");
    }
}
