package fr.atlasworld.network.exceptions.panel;

public class PanelServerNotExistException extends PanelException {
    public PanelServerNotExistException(String message) {
        super(message);
    }

    public PanelServerNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
