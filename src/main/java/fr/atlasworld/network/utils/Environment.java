package fr.atlasworld.network.utils;

import fr.atlasworld.network.AtlasNetwork;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Environment {
    private Terminal terminal;

    private Environment() {
        try {
            this.terminal = TerminalBuilder.terminal();
        } catch (IOException e) {
            this.terminal = null;
            AtlasNetwork.logger.warn("Environment does not have a terminal!", e);
        }
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public boolean hasTerminal() {
        return this.terminal != null;
    }

    //Static fields
    private static Environment environment;

    public static Environment getEnvironment() {
        if (environment == null) {
            environment = new Environment();
        }

        return environment;
    }
}
