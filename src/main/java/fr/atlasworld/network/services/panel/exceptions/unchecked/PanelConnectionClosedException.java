package fr.atlasworld.network.services.panel.exceptions.unchecked;

import fr.atlasworld.network.services.panel.exceptions.PanelException;

public class PanelConnectionClosedException extends UncheckedPanelException {
    public PanelConnectionClosedException(String message) {
        super(message, new PanelException("Panel connection closed."));
    }

    public PanelConnectionClosedException() {
        super(new PanelException("Panel connection closed."));
    }
}
