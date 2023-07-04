package fr.atlasworld.network.console;

import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

public abstract class ConsoleQuestion<T> {
    protected final Terminal terminal;
    protected final String message;

    public ConsoleQuestion(Terminal terminal, String message) {
        this.terminal = terminal;
        this.message = message;
    }

    public T ask() {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(this.terminal)
                .completer(this.getCompleter())
                .build();

        return this.parseAnswer(reader.readLine(this.message + "\n"));
    }

    protected abstract T parseAnswer(String answer);
    protected abstract Completer getCompleter();
}
