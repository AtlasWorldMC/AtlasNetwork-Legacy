package fr.atlasworld.network.exceptions.panel;

/**
 * PanelException, thrown when something related to the panel fails
 */
public class PanelException extends Exception {
    public PanelException(String message) {
        super(message);
    }

    public PanelException(String message, Throwable cause) {
        super(message, cause);
    }
}
