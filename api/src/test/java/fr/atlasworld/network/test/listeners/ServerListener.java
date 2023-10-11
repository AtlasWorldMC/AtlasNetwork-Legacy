package fr.atlasworld.network.test.listeners;

import fr.atlasworld.network.api.event.components.EventListener;
import fr.atlasworld.network.api.event.components.Listener;
import fr.atlasworld.network.api.event.server.ServerInitializeEvent;
import fr.atlasworld.network.api.event.server.ServerStoppingEvent;
import fr.atlasworld.network.api.exception.services.database.DatabaseException;
import fr.atlasworld.network.api.services.database.Database;
import fr.atlasworld.network.api.services.database.DatabaseService;
import fr.atlasworld.network.test.TestModule;
import fr.atlasworld.network.test.entities.TimeData;

public class ServerListener implements EventListener {
    @Listener
    public void onServerInit(ServerInitializeEvent event) throws DatabaseException {
        TestModule.logger().info("Saving system specifications..");

        DatabaseService service = event.getServer().getServiceManager().getDatabaseService();
        Database<TimeData> timeDataBase = service.getDatabase("time", TimeData.class);

        if (timeDataBase.has(TimeData.DATA_ID)) {
            TestModule.logger().info("Saved time: " + timeDataBase.load(TimeData.DATA_ID).getSavedTime());
        } else {
            timeDataBase.save(new TimeData(System.currentTimeMillis()));
            TestModule.logger().info("Saved time to database.");
        }
    }

    @Listener
    public void onServerStopping(ServerStoppingEvent event) {
        TestModule.logger().info("Shutting Test Module..");
    }
}
