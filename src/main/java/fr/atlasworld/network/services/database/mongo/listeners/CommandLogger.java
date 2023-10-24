package fr.atlasworld.network.services.database.mongo.listeners;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.boot.LaunchArgs;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CommandLogger implements CommandListener {
    private final LaunchArgs launchArgs;

    public CommandLogger(LaunchArgs launchArgs) {
        this.launchArgs = launchArgs;
    }

    public CommandLogger() {
        this(LaunchArgs.getLaunchArgs());
    }

    @Override
    public void commandSucceeded(@NotNull CommandSucceededEvent event) {
        if (this.launchArgs.databaseMonitoringEnabled()) {
            AtlasNetwork.logger.info("[Database-Monitor] SUCCESS: Request: '{}' | Time: {}ms | Command: {}",
                    event.getRequestId(),
                    event.getElapsedTime(TimeUnit.MILLISECONDS),
                    event.getCommandName());
            AtlasNetwork.logger.debug("[Database-Monitor] Request Context dump: {}", event.getRequestContext());
        }
    }

    @Override
    public void commandFailed(@NotNull CommandFailedEvent event) {
        AtlasNetwork.logger.warn("[Database-Monitor] Database command '{}' failed: ", event.getCommandName(), event.getThrowable());
    }
}
