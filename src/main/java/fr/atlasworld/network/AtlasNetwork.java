package fr.atlasworld.network;

import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.setup.WizardSetup;
import fr.atlasworld.network.utils.LaunchArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasNetwork {
    public static final Logger logger = LoggerFactory.getLogger("AtlasNetwork");
    public static LaunchArgs launchArgs;

    public static void main(String[] args) {
        AtlasNetwork.logger.info("Initializing AtlasNetwork...");
        //Handle Args
        launchArgs = new LaunchArgs(args);

        //Load configuration
        if (launchArgs.isForceConfig()) {
            AtlasNetwork.logger.warn("AtlasNetwork launched with argument '{}' overwriting already existent configuration.", LaunchArgs.FORCE_CONFIG);
            FileManager.getSettingsFile().delete();
        }

        if (!FileManager.getSettingsFile().exists()) {
            AtlasNetwork.logger.info("Opening wizard setup..");
            WizardSetup.init();
        }
    }
}
