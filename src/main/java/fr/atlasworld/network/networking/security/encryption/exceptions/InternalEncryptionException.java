package fr.atlasworld.network.networking.security.encryption.exceptions;

public class InternalEncryptionException extends EncryptionException {
    private static final String FEEDBACK = "INTERNAL_EXCEPTION";

    public InternalEncryptionException() {
        super(FEEDBACK);
    }

    public InternalEncryptionException(String message) {
        super(message, FEEDBACK);
    }

    public InternalEncryptionException(String message, Throwable cause) {
        super(message, cause, FEEDBACK);
    }

    public InternalEncryptionException(Throwable cause) {
        super(cause, FEEDBACK);
    }
}
