package fr.atlasworld.network;

import ch.qos.logback.classic.Level;
import com.mojang.brigadier.CommandDispatcher;
import fr.atlasworld.network.balancer.LoadBalancer;
import fr.atlasworld.network.balancer.haproxy.HAProxyLoadBalancer;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.command.CommandThread;
import fr.atlasworld.network.command.commands.AuthCommand;
import fr.atlasworld.network.command.commands.StopCommand;
import fr.atlasworld.network.config.Config;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.database.mongo.MongoDatabaseManager;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.exceptions.requests.RequestException;
import fr.atlasworld.network.networking.packet.*;
import fr.atlasworld.network.networking.security.authentication.NetworkAuthenticationManager;
import fr.atlasworld.network.networking.security.encryption.NetworkEncryptionManager;
import fr.atlasworld.network.networking.session.NetworkSessionManager;
import fr.atlasworld.network.networking.session.SessionManager;
import fr.atlasworld.network.networking.settings.NetworkSocketBuilder;
import fr.atlasworld.network.networking.socket.NetworkSocketManager;
import fr.atlasworld.network.networking.socket.SocketManager;
import fr.atlasworld.network.security.NetworkSecurityManager;
import fr.atlasworld.network.security.SecurityManager;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.pterodactyl.PteroServerManager;
import fr.atlasworld.network.utils.LaunchArgs;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

public class AtlasNetworkOld {
    public static final Logger logger = LoggerFactory.getLogger("AtlasNetwork");
    public static final CountDownLatch SOCKET_STARTED_LATCH = new CountDownLatch(1);
    private static LaunchArgs launchArgs;
    private static SocketManager socket;
    private static Config config;
    private static SecurityManager securityManager;
    private static DatabaseManager databaseManager;
    private static SessionManager sessionManager;
    private static ServerManager serverManager;
    private static LoadBalancer loadBalancer;
    private static CommandDispatcher<CommandSource> commandDispatcher;

    /**
     * Main Class, AtlasNetwork is an event driven application, so this only initializes everything.
     */
    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        AtlasNetworkOld.logger.info("Initializing AtlasNetwork...");



        //Handle Args
        launchArgs = new LaunchArgs(args);
        if (launchArgs.isDevEnv()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(logger.getName());
            rootLogger.setLevel(Level.DEBUG);
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        } else {
            ch.qos.logback.classic.Logger mongoLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.OFF);
        }

        AtlasNetworkOld.logger.info("Loading Configuration..");
        config = Config.getSettings();

        AtlasNetworkOld.logger.info("Initializing managers..");
        securityManager = new NetworkSecurityManager(config);
        databaseManager = new MongoDatabaseManager(config.database());
        sessionManager = new NetworkSessionManager();

        AtlasNetworkOld.logger.info("Connecting to the load balancer..");
        try {
            loadBalancer = new HAProxyLoadBalancer(config.balancer());
        } catch (RequestException e) {
            AtlasNetworkOld.logger.error("Unable to connect to the load balancer", e);
            System.exit(-1);
        }

        AtlasNetworkOld.logger.info("Connecting with the panel..");
        try {
            serverManager = new PteroServerManager(
                    databaseManager.getServerDatabase(),
                    config.panel(),
                    loadBalancer,
                    databaseManager.getAuthenticationProfileDatabase());
            serverManager.initialize();
        } catch (DatabaseException | PanelException e) {
            AtlasNetworkOld.logger.error("Unable to connect to the panel", e);
            System.exit(-1);
        }

        //Handles the command execution
        AtlasNetworkOld.logger.info("Starting command handler..");
        commandDispatcher = new CommandDispatcher<>();
        registerCommands(commandDispatcher);
        CommandThread commandThread = new CommandThread(commandDispatcher);
        commandThread.setDaemon(true);
        commandThread.start();
        AtlasNetworkOld.logger.info("Command handler started..");

        //Init Socket
        AtlasNetworkOld.logger.info("Initializing Socket...");
        PacketManager packetManager = new NetworkPacketManager();
        try {
            Database<AuthenticationProfile> authDatabase = databaseManager.getAuthenticationProfileDatabase();
            NetworkSocketBuilder socketSettings = new NetworkSocketBuilder(
                    config,
                    sessionManager,
                    () -> new NetworkEncryptionManager(securityManager),
                    () -> new NetworkAuthenticationManager(securityManager, authDatabase),
                    packetManager);
            socket = new NetworkSocketManager(socketSettings);
        } catch (Exception e) {
            AtlasNetworkOld.logger.error("Unable to start socket", e);
            System.exit(-1);
        }

        AtlasNetworkOld.logger.info("Network Initialized.");

        //Boot up Socket
        AtlasNetworkOld.logger.info("Starting socket..");
        registerPackets(packetManager);
        socket.bind().sync();
        AtlasNetworkOld.SOCKET_STARTED_LATCH.countDown();
        AtlasNetworkOld.logger.info("Socket started.");

        AtlasNetworkOld.logger.info("Ready, Waiting for connections.");

        //Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AtlasNetworkOld.logger.info("Stopping socket..");
            socket.unbind();
            AtlasNetworkOld.logger.info("Socket stopped.");
            AtlasNetworkOld.logger.info("Terminating Database connection..");
            databaseManager.close();
            AtlasNetworkOld.logger.info("Good Bye!");
        }));
    }

    private static void registerPackets(PacketManager packetManager) {
        packetManager.register(new HelloWorldPacket());
        packetManager.register(new InitializePacket());
        packetManager.register(new CreateServerPacket(serverManager));
    }


    private static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        StopCommand.register(dispatcher);
        AuthCommand.register(dispatcher); // Auth Command Deprecated
    }

    public static LaunchArgs getLaunchArgs() {
        return launchArgs;
    }

    public static SocketManager getSocket() {
        return socket;
    }

    public static Config getConfig() {
        return config;
    }

    public static SecurityManager getSecurityManager() {
        return securityManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    public static LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public static CommandDispatcher<CommandSource> getCommandDispatcher() {
        return commandDispatcher;
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }
}
