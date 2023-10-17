package fr.atlasworld.network.config.exceptions;

public class ConfigurationParsingException extends ConfigurationException {
    public ConfigurationParsingException() {
    }

    public ConfigurationParsingException(String message) {
        super(message);
    }

    public ConfigurationParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationParsingException(Throwable cause) {
        super(cause);
    }
}
