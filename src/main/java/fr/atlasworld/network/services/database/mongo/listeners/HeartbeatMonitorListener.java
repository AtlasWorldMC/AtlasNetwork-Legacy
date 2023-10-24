package fr.atlasworld.network.services.database.mongo.listeners;

import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.boot.LaunchArgs;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class HeartbeatMonitorListener implements ServerMonitorListener {
    private final LaunchArgs launchArgs;
    private final long heartbeatFrequency;

    public HeartbeatMonitorListener(LaunchArgs launchArgs, long heartbeatFrequency) {
        this.launchArgs = launchArgs;
        this.heartbeatFrequency = heartbeatFrequency;
    }

    public HeartbeatMonitorListener(long heartbeatFrequency) {
        this(LaunchArgs.getLaunchArgs(), heartbeatFrequency);
    }

    @Override
    public void serverHeartbeatSucceeded(@NotNull ServerHeartbeatSucceededEvent event) {
        Thread.currentThread().setName("Database-Heartbeat-Monitor");
        long ping = (event.getElapsedTime(TimeUnit.MILLISECONDS) - TimeUnit.MILLISECONDS.convert(this.heartbeatFrequency, TimeUnit.SECONDS));

        if (ping > 30) {
            AtlasNetwork.logger.warn("Database response took more than 30ms! Took: {}ms", ping);
            return;
        }

        if (this.launchArgs.databaseMonitoringEnabled()) {
            AtlasNetwork.logger.info("Database responded in {}ms", ping);
        }
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
        Thread.currentThread().setName("Database-Heartbeat-Monitor");
        AtlasNetwork.logger.warn("Database heartbeat check failed: ", event.getThrowable());
    }
}
