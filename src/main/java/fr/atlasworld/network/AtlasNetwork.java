package fr.atlasworld.network;

import ch.qos.logback.classic.Level;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.networking.securty.encryption.NetworkEncryptionManager;
import fr.atlasworld.network.networking.session.NetworkSessionManager;
import fr.atlasworld.network.networking.socket.NetworkSocketManager;
import fr.atlasworld.network.networking.settings.NetworkSocketSettings;
import fr.atlasworld.network.networking.socket.SocketManager;
import fr.atlasworld.network.old_networking.packet.HelloWorldPacket;
import fr.atlasworld.network.old_networking.packet.PacketManager;
import fr.atlasworld.network.security.NetworkSecurityManager;
import fr.atlasworld.network.security.SecurityManager;
import fr.atlasworld.network.utils.LaunchArgs;
import fr.atlasworld.network.utils.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class AtlasNetwork {
    public static final Logger logger = LoggerFactory.getLogger("AtlasNetwork");
    private static LaunchArgs launchArgs;
    private static SocketManager socket;
    private static Settings settings;
    private static SecurityManager securityManager;

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        AtlasNetwork.logger.info("Initializing AtlasNetwork...");

        //Handle Args
        launchArgs = new LaunchArgs(args);

        if (launchArgs.isDevEnv()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(logger.getName());
            rootLogger.setLevel(Level.DEBUG);
        }

        //Load configuration
        if (launchArgs.isForceConfig()) {
            AtlasNetwork.logger.warn("AtlasNetwork launched with argument '{}' overriding already existent configuration.", LaunchArgs.FORCE_CONFIG);
            FileManager.getSettingsFile().delete();
        }
        settings = Settings.getSettings();

        securityManager = new NetworkSecurityManager();

        //Init Socket
        NetworkSocketSettings socketSettings = new NetworkSocketSettings(
                settings,
                new NetworkSessionManager(new HashMap<>()),
                () -> new NetworkEncryptionManager(securityManager));

        socket = new NetworkSocketManager(socketSettings);
        registerPackets(PacketManager.getManager());

        AtlasNetwork.logger.info("Network Initialized.");


        //Boot up Socket
        AtlasNetwork.logger.info("Starting socket...");
        socket.bind().sync();
        AtlasNetwork.logger.info("Socket started.");

        AtlasNetwork.logger.info("Ready, Waiting for connections.");

        //Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AtlasNetwork.logger.info("Stopping socket..");
            socket.unbind();
            AtlasNetwork.logger.info("Socket stopped.");
        }));
    }


    private static void registerPackets(PacketManager manager) {
        AtlasNetwork.logger.info("Registering packets...");
        manager.registerPacket(new HelloWorldPacket());
        AtlasNetwork.logger.info("Packets registered!");
    }

    public static LaunchArgs getLaunchArgs() {
        return launchArgs;
    }

    public static SocketManager getSocket() {
        return socket;
    }

    public static Settings getSettings() {
        return settings;
    }
    public static SecurityManager getSecurityManager() {
        return securityManager;
    }
}
