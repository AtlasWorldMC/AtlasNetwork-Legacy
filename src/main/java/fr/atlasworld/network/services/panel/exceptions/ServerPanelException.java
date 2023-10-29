package fr.atlasworld.network.services.panel.exceptions;

import fr.atlasworld.network.services.panel.entities.PanelServer;

/**
 * Thrown in case something went wrong with a server on the panel
 */
public class ServerPanelException extends PanelException {
    public ServerPanelException(PanelServer server) {
        super("Something went wrong with '" + server.name() + "'");
    }

    public ServerPanelException(String message) {
        super(message);
    }

    public ServerPanelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerPanelException(Throwable cause, PanelServer server) {
        super("Something went wrong with '" + server.name() + "'", cause);
    }
}
