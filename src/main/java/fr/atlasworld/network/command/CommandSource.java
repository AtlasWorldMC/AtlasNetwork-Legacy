package fr.atlasworld.network.command;

import org.slf4j.Logger;

public class CommandSource {
    private final Logger logger;

    public CommandSource(Logger logger) {
        this.logger = logger;
    }

    public void sendError(String message) {
        this.logger.error(message);
    }

    public void sendError(String message, Object... objects) {
        this.logger.error(message, objects);
    }

    public void sendMessage(String message) {
        this.logger.info(message);
    }

    public void sendMessage(String message, Object... objects) {
        this.logger.info(message, objects);
    }

    public Logger getLogger() {
        return logger;
    }
}
