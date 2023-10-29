package fr.atlasworld.network.services.panel.exceptions.unchecked;

import fr.atlasworld.network.services.panel.exceptions.PanelException;

public class UnknownServerSchemaPanelException extends UncheckedPanelException {
    public UnknownServerSchemaPanelException(String message) {
        super(new PanelException(message));
    }
}
