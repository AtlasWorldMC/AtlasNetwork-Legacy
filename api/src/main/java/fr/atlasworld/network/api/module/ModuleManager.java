package fr.atlasworld.network.api.module;

import fr.atlasworld.network.api.event.components.Event;
import fr.atlasworld.network.api.event.components.EventListener;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface ModuleManager {

    /**
     * Retrieves all the loaded modules.
     * @return loaded modules
     */
    Collection<Module> getLoadedModules();

    /**
     * Registers a listener
     * @param listener listener
     * @param module module that registered the listener
     */
    void registerListener(EventListener listener, Module module);

    /**
     * Calls an event synchronously, blocks until al listeners has finished execution.
     * @param event event to trigger
     */
    void callEvent(Event event);

    /**
     * Calls an event asynchronously and returns the event it once all the listeners are executed.
     * @param event event to call
     * @return future of the event
     */
    CompletableFuture<Event> callEventAsync(Event event);
}
