package fr.atlasworld.network.networking.security.encryption.exceptions;

import fr.atlasworld.network.networking.exceptions.NetworkException;

public class EncryptionException extends NetworkException {
    private final String networkFeedback;

    public EncryptionException(String networkFeedback) {
        this.networkFeedback = networkFeedback;
    }

    public EncryptionException(String message, String networkFeedback) {
        super(message);
        this.networkFeedback = networkFeedback;
    }

    public EncryptionException(String message, Throwable cause, String networkFeedback) {
        super(message, cause);
        this.networkFeedback = networkFeedback;
    }

    public EncryptionException(Throwable cause, String networkFeedback) {
        super(cause);
        this.networkFeedback = networkFeedback;
    }

    public String getNetworkFeedback() {
        return networkFeedback;
    }
}
