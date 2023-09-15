package fr.atlasworld.network.exceptions.panel;

/**
 * Thrown when trying to use a server that doesn't exist
 */
public class PanelServerNotExistException extends PanelException {
    public PanelServerNotExistException(String message) {
        super(message);
    }

    public PanelServerNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
