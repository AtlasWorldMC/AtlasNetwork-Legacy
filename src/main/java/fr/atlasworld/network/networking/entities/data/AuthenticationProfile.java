package fr.atlasworld.network.networking.entities.data;

import fr.atlasworld.network.services.database.DatabaseData;
import fr.atlasworld.network.services.database.DatabaseEntityFactory;
import fr.atlasworld.network.services.database.DatabaseEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record AuthenticationProfile(UUID id, String tokenHash) implements DatabaseEntity<AuthenticationProfile> {
    @Override
    public @NotNull DatabaseData asData() {
        return new DatabaseData("token_hash", this.tokenHash);
    }

    public static final class AuthenticationProfileFactory implements DatabaseEntityFactory<AuthenticationProfile> {
        private AuthenticationProfileFactory() {} //Singleton

        @Override
        public @NotNull AuthenticationProfile create(DatabaseData data, UUID id) {
            return new AuthenticationProfile(id, (String) data.get("token_hash"));
        }

        private static AuthenticationProfileFactory factory;

        public static AuthenticationProfileFactory getFactory() {
            if (factory == null) {
                factory = new AuthenticationProfileFactory();
            }

            return factory;
        }
    }
}
