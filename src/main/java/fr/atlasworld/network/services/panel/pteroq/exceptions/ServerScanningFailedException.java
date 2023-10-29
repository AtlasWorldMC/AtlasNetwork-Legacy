package fr.atlasworld.network.services.panel.pteroq.exceptions;

import fr.atlasworld.network.services.panel.entities.PanelServer;
import fr.atlasworld.network.services.panel.exceptions.ServerPanelException;

public class ServerScanningFailedException extends ServerPanelException {
    public ServerScanningFailedException(PanelServer server) {
        super("'" + server.name() + "' Server Scanning failed.");
    }

    public ServerScanningFailedException(String message) {
        super(message);
    }

    public ServerScanningFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerScanningFailedException(Throwable cause, PanelServer server) {
        super("'" + server.name() + "' Server Scanning failed.", cause);
    }
}
