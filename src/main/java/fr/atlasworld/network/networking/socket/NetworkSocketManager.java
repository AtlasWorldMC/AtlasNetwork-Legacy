package fr.atlasworld.network.networking.socket;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.settings.SocketBuilder;
import io.netty.channel.ChannelFuture;

/**
 * Network implementation of the SocketManager
 * @see SocketManager
 */
public class NetworkSocketManager implements SocketManager {
    private final SocketBuilder settings;
    private boolean bound;

    public NetworkSocketManager(SocketBuilder settings) {
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

                    future.channel().closeFuture().addListener((ChannelFuture closeFuture) -> {
                        this.bound = false;
                        this.settings.cleanUp();

                        if (future.cause() == null) {
                            AtlasNetwork.logger.info("[Socket] Disconnected");
                        } else {
                            AtlasNetwork.logger.error("[Socket] Disconnected: ", future.cause());
                        }
                    });
                });
    }

    @Override
    public void unbind() {
        this.settings.cleanUp();
    }
}
