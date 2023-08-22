package fr.atlasworld.network.exceptions.panel;

public class PanelException extends Exception {
    public PanelException(String message) {
        super(message);
    }

    public PanelException(String message, Throwable cause) {
        super(message, cause);
    }
}
