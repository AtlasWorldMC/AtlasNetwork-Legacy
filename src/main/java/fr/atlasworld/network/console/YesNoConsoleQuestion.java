package fr.atlasworld.network.console;

import fr.atlasworld.network.AtlasNetwork;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

public class YesNoConsoleQuestion extends ConsoleQuestion<Boolean> {
    public YesNoConsoleQuestion(Terminal terminal, String message) {
        super(terminal, message);
    }

    @Override
    protected Boolean parseAnswer(String answer) {
        if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
            return true;
        }

        if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
            return false;
        }

        AtlasNetwork.logger.warn("Please respond with yes or no! {} is not a valid answer!", answer);
        return this.ask();
    }

    @Override
    protected Completer getCompleter() {
        return new StringsCompleter("yes", "no");
    }
}
