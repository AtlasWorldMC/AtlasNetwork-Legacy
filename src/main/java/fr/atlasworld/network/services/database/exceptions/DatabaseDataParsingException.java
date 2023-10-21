package fr.atlasworld.network.services.database.exceptions;

public class DatabaseDataParsingException extends DatabaseException {

    public DatabaseDataParsingException(String message) {
        super(message);
    }

    public DatabaseDataParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseDataParsingException(Throwable cause, Object object) {
        super("Could not parse '" + object + "'.", cause);
    }
}
