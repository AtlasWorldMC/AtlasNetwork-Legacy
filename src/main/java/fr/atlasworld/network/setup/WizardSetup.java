package fr.atlasworld.network.setup;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.console.YesNoConsoleQuestion;
import fr.atlasworld.network.utils.Environment;

public class WizardSetup {
    public static void init() {
        Environment env = Environment.getEnvironment();

        if (!env.hasTerminal()) {
            AtlasNetwork.logger.error("Unable to proceed, no terminal available in this environment.");
            return;
        }

        //Is primary Node
        boolean primaryNode = new YesNoConsoleQuestion(env.getTerminal(),
                "Is this the primary node of the network?").ask();

        System.out.println(primaryNode);
    }
}
