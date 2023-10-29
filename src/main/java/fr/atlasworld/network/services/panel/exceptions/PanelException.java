package fr.atlasworld.network.services.panel.exceptions;

import fr.atlasworld.network.services.exception.ServiceException;

public class PanelException extends ServiceException {
    public PanelException() {
    }

    public PanelException(String message) {
        super(message);
    }

    public PanelException(String message, Throwable cause) {
        super(message, cause);
    }

    public PanelException(Throwable cause) {
        super(cause);
    }
}
