package fr.atlasworld.network.api.event.networking;

import fr.atlasworld.network.api.event.components.CancellableEvent;
import fr.atlasworld.network.api.event.components.Event;

public class NetworkEvent implements Event, CancellableEvent {
    private boolean cancelled = false;



    @Override
    public boolean cancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
