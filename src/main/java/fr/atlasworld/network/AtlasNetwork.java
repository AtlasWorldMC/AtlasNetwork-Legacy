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
import fr.atlasworld.network.networking.packet.HelloWorldPacket;
import fr.atlasworld.network.networking.packet.NetworkPacketManager;
import fr.atlasworld.network.networking.packet.PacketManager;
import fr.atlasworld.network.networking.security.authentication.NetworkAuthenticationManager;
import fr.atlasworld.network.networking.security.encryption.NetworkEncryptionManager;
import fr.atlasworld.network.networking.session.NetworkSessionManager;
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

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class AtlasNetwork {
    public static final Logger logger = LoggerFactory.getLogger("AtlasNetwork");
    private static LaunchArgs launchArgs;
    private static SocketManager socket;
    private static Config config;
    private static SecurityManager securityManager;
    private static DatabaseManager databaseManager;
    private static ServerManager serverManager;
    private static LoadBalancer loadBalancer;
    private static CommandDispatcher<CommandSource> commandDispatcher;

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        AtlasNetwork.logger.info("Initializing AtlasNetwork...");

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



        AtlasNetwork.logger.info("Loading Configuration..");
        config = Config.getSettings();

        AtlasNetwork.logger.info("Initializing managers..");
        securityManager = new NetworkSecurityManager(config);
        databaseManager = new MongoDatabaseManager(config.database());

        AtlasNetwork.logger.info("Connecting to the load balancer..");
        try {
            loadBalancer = new HAProxyLoadBalancer(
                    "https://balance-api.atlasworld.fr",
                    "mc_proxies",
                    "network",
                    "OAncZCM22QdGdsgvXkhhAOhi2ikv"
            );
        } catch (RequestException e) {
            AtlasNetwork.logger.error("Unable to connect to the load balancer", e);
            System.exit(-1);
        }

        AtlasNetwork.logger.info("Connecting with the panel..");
        try {
            serverManager = new PteroServerManager(
                    databaseManager.getServerDatabase(),
                    config.panel(),
                    loadBalancer
            );
            serverManager.initialize();
        } catch (DatabaseException | PanelException e) {
            AtlasNetwork.logger.error("Unable to connect to the panel", e);
            System.exit(-1);
        }

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
        try {
            Database<AuthenticationProfile> authDatabase = databaseManager.getAuthenticationProfileDatabase();
            NetworkSocketBuilder socketSettings = new NetworkSocketBuilder(
                    config,
                    new NetworkSessionManager(),
                    () -> new NetworkEncryptionManager(securityManager),
                    () -> new NetworkAuthenticationManager(securityManager, authDatabase),
                    packetManager);
            socket = new NetworkSocketManager(socketSettings);
        } catch (Exception e) {
            AtlasNetwork.logger.error("Unable to start socket", e);
            System.exit(-1);
        }

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

    public static Config getConfig() {
        return config;
    }

    public static SecurityManager getSecurityManager() {
        return securityManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static CommandDispatcher<CommandSource> getCommandDispatcher() {
        return commandDispatcher;
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }
}
