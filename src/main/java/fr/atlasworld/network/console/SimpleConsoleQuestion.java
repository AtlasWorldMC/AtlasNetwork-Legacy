package fr.atlasworld.network.console;

import fr.atlasworld.network.AtlasNetwork;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

import java.util.Arrays;
import java.util.List;

//NOT RECOMMENDED IN PRODUCTION FOR QUESTION OVER 2 ANSWERS.
//USING THIS FOR OVER 2 ANSWERS COULD BECOME SWITCH HELL.
//RECOMMENDED TO MAKE A CUSTOM CONSOLE QUESTION IMPLEMENTATION.
public class SimpleConsoleQuestion extends ConsoleQuestion<String> {
    private final List<String> answers;
    public SimpleConsoleQuestion(Terminal terminal, String message, List<String> answers) {
        super(terminal, message);
        this.answers = answers;
    }

    public SimpleConsoleQuestion(Terminal terminal, String message, String... answers) {
        super(terminal, message);
        this.answers = Arrays.asList(answers);
    }

    @Override
    protected String parseAnswer(String answer) {
        if (!this.answers.contains(answer)) {
            AtlasNetwork.logger.warn("{} does not match any of the options!", answer);
            return this.ask();
        }

        return answer;
    }

    @Override
    protected Completer getCompleter() {
        return new StringsCompleter(this.answers);
    }
}
