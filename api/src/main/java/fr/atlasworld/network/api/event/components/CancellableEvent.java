package fr.atlasworld.network.api.event.components;

public interface CancellableEvent {
    boolean cancelled();
    void setCancelled(boolean cancelled);
}
