package fr.atlasworld.network.command;

import org.slf4j.Logger;

/**
 * Command Source, source of the command, usually it's the console
 */
public class CommandSource {
    private final Logger logger;

    public CommandSource(Logger logger) {
        this.logger = logger;
    }

    /**
     * Sends an error to the source
     * @param message error message
     */
    public void sendError(String message) {
        this.logger.error(message);
    }

    /**
     * Sends an error to the source
     * @param message error message
     */
    public void sendError(String message, Object... objects) {
        this.logger.error(message, objects);
    }

    /**
     * Sends a message to the source
     * @param message message
     */
    public void sendMessage(String message) {
        this.logger.info(message);
    }

    /**
     * Sends a message to the source
     * @param message message
     */
    public void sendMessage(String message, Object... objects) {
        this.logger.info(message, objects);
    }

    public Logger getLogger() {
        return logger;
    }
}
