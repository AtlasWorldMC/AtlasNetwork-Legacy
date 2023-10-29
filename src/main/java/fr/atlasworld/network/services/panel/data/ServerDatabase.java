package fr.atlasworld.network.services.panel.data;

import fr.atlasworld.network.services.database.DatabaseEntityFactory;
import fr.atlasworld.network.services.database.entities.DatabaseData;
import fr.atlasworld.network.services.database.entities.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerDatabase(UUID id, String schemaId, boolean external) implements DatabaseEntity<ServerDatabase> {
    @Override
    public @NotNull DatabaseData asData() {
        DatabaseData data = new DatabaseData();

        data.put("schema_id", this.schemaId);
        data.put("external", this.external);

        return data;
    }

    public static final class ServerDatabaseFactory implements DatabaseEntityFactory<ServerDatabase> {
        private ServerDatabaseFactory() {} //Singleton

        @Override
        public @NotNull ServerDatabase create(DatabaseData data, UUID id) {
            return new ServerDatabase(id, (String) data.get("schema_id"), (Boolean) data.get("external"));
        }

        private static ServerDatabaseFactory factory;

        public static ServerDatabaseFactory getFactory() {
            if (factory == null) {
                factory = new ServerDatabaseFactory();
            }

            return factory;
        }
    }
}
