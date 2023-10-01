package fr.atlasworld.network.api.event.networking;

import fr.atlasworld.network.api.event.components.Event;
import fr.atlasworld.network.api.networking.NetworkSocket;

public class NetworkEvent implements Event {
    private final NetworkSocket socket;

    public NetworkEvent(NetworkSocket socket) {
        this.socket = socket;
    }

    public NetworkSocket getSocket() {
        return socket;
    }
}
