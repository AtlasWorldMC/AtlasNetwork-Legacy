package fr.atlasworld.network.services.database.exceptions.unchecked;

import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import fr.atlasworld.network.services.exception.unchecked.UncheckedServiceException;

public class UncheckedDatabaseException extends UncheckedServiceException {
    public UncheckedDatabaseException(String message, DatabaseException cause) {
        super(message, cause);
    }

    public UncheckedDatabaseException(DatabaseException cause) {
        super(cause);
    }
}
