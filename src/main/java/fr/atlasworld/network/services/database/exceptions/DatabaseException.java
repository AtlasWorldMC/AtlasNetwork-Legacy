package fr.atlasworld.network.services.database.exceptions;

import fr.atlasworld.network.services.exception.ServiceException;

public class DatabaseException extends ServiceException {
    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
