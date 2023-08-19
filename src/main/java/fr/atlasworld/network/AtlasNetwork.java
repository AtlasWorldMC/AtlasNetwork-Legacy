package fr.atlasworld.network;

import ch.qos.logback.classic.Level;
import com.mojang.brigadier.CommandDispatcher;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.command.CommandThread;
import fr.atlasworld.network.command.commands.AuthCommand;
import fr.atlasworld.network.command.commands.StopCommand;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.database.mongo.MongoDatabaseManager;
import fr.atlasworld.network.networking.packet.HelloWorldPacket;
import fr.atlasworld.network.networking.packet.NetworkPacketManager;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.security.authentication.NetworkAuthenticationManager;
import fr.atlasworld.network.networking.security.encryption.NetworkEncryptionManager;
import fr.atlasworld.network.networking.session.NetworkSessionManager;
import fr.atlasworld.network.networking.socket.NetworkSocketManager;
import fr.atlasworld.network.networking.settings.NetworkSocketSettings;
import fr.atlasworld.network.networking.socket.SocketManager;
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
    private static DatabaseManager databaseManager;
    private static CommandDispatcher<CommandSource> commandDispatcher;

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        AtlasNetwork.logger.info("Initializing AtlasNetwork...");

        //Handle Args
        launchArgs = new LaunchArgs(args);
        if (launchArgs.isDevEnv()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(logger.getName());
            rootLogger.setLevel(Level.DEBUG);
        } else {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("org.mongodb.driver");
            rootLogger.setLevel(Level.OFF);
        }

        AtlasNetwork.logger.info("Loading Configuration..");
        settings = Settings.getSettings();

        AtlasNetwork.logger.info("Initializing connections managers..");
        securityManager = new NetworkSecurityManager(settings);
        databaseManager = new MongoDatabaseManager(settings.getDatabase());


        //Handles the command execution
        AtlasNetwork.logger.info("Starting command handler..");
        commandDispatcher = new CommandDispatcher<>();
        registerCommands(commandDispatcher);
        CommandThread commandThread = new CommandThread(commandDispatcher);
        commandThread.setDaemon(true);
        commandThread.start();
        AtlasNetwork.logger.info("Command handler started..");

        //Init Socket
        AtlasNetwork.logger.info("Initializing Socket...");
        PacketManager packetManager = new NetworkPacketManager();
        NetworkSocketSettings socketSettings = new NetworkSocketSettings(
                settings,
                new NetworkSessionManager(new HashMap<>()),
                () -> new NetworkEncryptionManager(securityManager),
                () -> new NetworkAuthenticationManager(securityManager, databaseManager),
                packetManager);

        socket = new NetworkSocketManager(socketSettings);

        AtlasNetwork.logger.info("Network Initialized.");


        //Boot up Socket
        AtlasNetwork.logger.info("Starting socket..");
        registerPackets(packetManager);
        socket.bind().sync();
        AtlasNetwork.logger.info("Socket started.");
        AtlasNetwork.logger.info("Ready, Waiting for connections.");

        //Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AtlasNetwork.logger.info("Stopping socket..");
            socket.unbind();
            AtlasNetwork.logger.info("Socket stopped.");
            AtlasNetwork.logger.info("Terminating Database connection..");
            databaseManager.close();
            AtlasNetwork.logger.info("Good Bye!");
        }));
    }

    private static void registerPackets(PacketManager packetManager) {
        packetManager.register(new HelloWorldPacket());
    }

    private static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        StopCommand.register(dispatcher);
        AuthCommand.register(dispatcher);
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

    public static CommandDispatcher<CommandSource> getCommandDispatcher() {
        return commandDispatcher;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
