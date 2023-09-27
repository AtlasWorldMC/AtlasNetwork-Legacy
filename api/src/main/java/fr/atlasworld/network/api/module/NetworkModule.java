package fr.atlasworld.network.api.module;

import fr.atlasworld.network.api.AtlasNetworkServer;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.logging.LogUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;

/**
 * Implement this interface on your main module class.
 * This interface defines where your module starts.
 * Don't forget to define it in your module.json file.
 */
public abstract class NetworkModule implements Module {
    private boolean initialized = false;
    private Module module;

    /**
     * Initialize function, called when your module initializes.
     * It's highly recommended to not interact with the api at this state, AtlasNetwork is not initialized yet.
     * Register your event listeners here too.
     * If your module is a driver start your services here and register your manager implementation.
     * @param server instance of AtlasNetwork
     * @throws ModuleException throw this if you could not correctly initialize or one of your services failed.
     */
    protected abstract void initialize(AtlasNetworkServer server) throws ModuleException;

    /**
     * Retrieve the module's logger
     * @return module's logger
     */
    public Logger getLogger() {
        return LogUtils.getLogger(this.module);
    }

    /**
     * DO NOT INVOKE THIS FUNCTION, THIS SHOULD ONLY BE TRIGGERED BY ATLAS-NETWORK!
     * Any changes on this function won't be notified in the changelogs
     */
    @ApiStatus.Internal
    public void startModule(AtlasNetworkServer server, Module module) throws ModuleException {
        if (this.initialized) {
            throw new UnsupportedOperationException("Cannot initialize a module multiple times!");
        }

        this.module = module;
        this.initialize(server);
        this.initialized = true;
    }

    @Override
    public @NotNull String getName() {
        return this.module.getName();
    }

    @Override
    public @NotNull String getId() {
        return this.module.getId();
    }

    @Override
    public @NotNull String getVersion() {
        return this.module.getVersion();
    }

    @Override
    public @Nullable String getDescription() {
        return this.module.getDescription();
    }

    @Override
    public @NotNull File getFile() {
        return this.module.getFile();
    }
}
