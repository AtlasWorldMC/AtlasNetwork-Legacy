package fr.atlasworld.network.config.exceptions;

import java.io.IOException;

public class ConfigurationLoadingException extends ConfigurationException {
    public ConfigurationLoadingException() {
    }

    public ConfigurationLoadingException(String message) {
        super(message);
    }

    public ConfigurationLoadingException(String message, IOException cause) {
        super(message, cause);
    }

    public ConfigurationLoadingException(IOException cause) {
        super(cause);
    }
}
