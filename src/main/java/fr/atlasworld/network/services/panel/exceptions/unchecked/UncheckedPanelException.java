package fr.atlasworld.network.services.panel.exceptions.unchecked;

import fr.atlasworld.network.services.exception.unchecked.UncheckedServiceException;
import fr.atlasworld.network.services.panel.exceptions.PanelException;

public class UncheckedPanelException extends UncheckedServiceException {
    public UncheckedPanelException(String message, PanelException cause) {
        super(message, cause);
    }

    public UncheckedPanelException(PanelException cause) {
        super(cause);
    }
}
