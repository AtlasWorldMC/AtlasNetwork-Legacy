package fr.atlasworld.network;

import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.networking.SocketManager;
import fr.atlasworld.network.utils.LaunchArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasNetwork {
    public static final Logger logger = LoggerFactory.getLogger("AtlasNetwork");
    public static LaunchArgs launchArgs;

    public static void main(String[] args) throws InterruptedException {
        AtlasNetwork.logger.info("Initializing AtlasNetwork...");
        //Handle Args
        launchArgs = new LaunchArgs(args);

        //Load configuration
        if (launchArgs.isForceConfig()) {
            AtlasNetwork.logger.warn("AtlasNetwork launched with argument '{}' overriding already existent configuration.", LaunchArgs.FORCE_CONFIG);
            FileManager.getSettingsFile().delete();
        }

        //Boot up Socket
        AtlasNetwork.logger.info("Starting socket...");
        SocketManager socketManager = SocketManager.getManager();

        socketManager.bind().sync();
        AtlasNetwork.logger.info("Network initialized and waiting for connections..");
    }

}
