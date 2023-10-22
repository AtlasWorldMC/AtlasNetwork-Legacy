package fr.atlasworld.network.services.database.mongo.exceptions.unchecked;

import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import fr.atlasworld.network.services.database.exceptions.unchecked.UncheckedDatabaseException;

public class UnsupportedMongoDBAuthenticationMechanism extends UncheckedDatabaseException {
    public UnsupportedMongoDBAuthenticationMechanism(String message) {
        super(new DatabaseException(message));
    }

    public static UnsupportedMongoDBAuthenticationMechanism forDeprecated(String mechanism) {
        return new UnsupportedMongoDBAuthenticationMechanism("'" + mechanism + "' is deprecated and should not be used anymore.");
    }
}
