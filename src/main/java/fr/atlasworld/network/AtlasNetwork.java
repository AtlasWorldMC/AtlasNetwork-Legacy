package fr.atlasworld.network;

import com.mattmalec.pterodactyl4j.exceptions.HttpException;
import fr.atlasworld.network.boot.NetworkBootstrap;
import fr.atlasworld.network.config.ConfigurationManager;
import fr.atlasworld.network.logging.LogUtils;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.socket.SocketManager;
import fr.atlasworld.network.security.SecurityManager;
import fr.atlasworld.network.services.panel.PanelService;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;

public class AtlasNetwork {
    public static final Logger logger = LogUtils.getLogger();
    private static AtlasNetwork instance;

    private final ConfigurationManager configurationManager;
    private final SocketManager socketManager;
    private final SecurityManager securityManager;
    private final PacketManager packetManager;
    private final PanelService panelService;

    private boolean started = false;

    public AtlasNetwork(ConfigurationManager configurationManager, SocketManager socketManager, SecurityManager securityManager, PacketManager packetManager, PanelService panelService) {
        this.panelService = panelService;
        if (instance != null) {
            throw new UnsupportedOperationException("Only on AtlasNetwork Server instance can be created!");
        }

        this.configurationManager = configurationManager;
        this.socketManager = socketManager;
        this.securityManager = securityManager;
        this.packetManager = packetManager;
    }

    public void start() throws Throwable {
        if (this.started) {
            throw new UnsupportedOperationException("AtlasNetwork cannot be started multiple times!");
        }

        // Socket
        AtlasNetwork.logger.info("Starting socket..");
        ChannelFuture futureResults = this.socketManager.start().sync();

        if (!futureResults.isSuccess()) {
            AtlasNetwork.logger.info("Failed to bind to {}.", this.socketManager.getFullAddress());
            throw futureResults.cause();
        } else {
            AtlasNetwork.logger.info("Socket started.");
        }

        // Services
        AtlasNetwork.logger.info("Starting services..");

        try {
            this.panelService.start();

            this.panelService.createServer("Test", "build_server");
        } catch (HttpException e) {
            AtlasNetwork.logger.error("Unable to make requests to the panel, please verify your configuration.");
            NetworkBootstrap.crash(e);
        }

        this.started = true;
        instance = this;
    }

    public void stop() {
        if (!this.started) {
            throw new UnsupportedOperationException("AtlasNetwork is only able to be stopped when its running.");
        }

        AtlasNetwork.logger.info("Shutting socket down..");
        this.socketManager.stop();
        AtlasNetwork.logger.info("Socket stopped.");

        AtlasNetwork.logger.info("AtlasNetwork stopped.");
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public static AtlasNetwork getServer() {
        if (instance == null) {
            throw new UnsupportedOperationException("AtlasNetwork did not finish initialization!");
        }

        return instance;
    }
}
