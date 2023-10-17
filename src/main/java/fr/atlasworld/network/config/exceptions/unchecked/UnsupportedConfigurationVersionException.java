package fr.atlasworld.network.config.exceptions.unchecked;

public class UnsupportedConfigurationVersionException extends UnsupportedOperationException {
    public UnsupportedConfigurationVersionException() {
    }

    public UnsupportedConfigurationVersionException(String message) {
        super(message);
    }

    public UnsupportedConfigurationVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedConfigurationVersionException(Throwable cause) {
        super(cause);
    }
}
