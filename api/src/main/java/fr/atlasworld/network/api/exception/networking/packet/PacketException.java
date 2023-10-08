package fr.atlasworld.network.api.exception.networking.packet;

import fr.atlasworld.network.api.exception.networking.requests.RequestFailException;

public class PacketException extends RequestFailException {
    public PacketException(String networkFeedback) {
        super(networkFeedback);
    }

    public PacketException(String message, String networkFeedback) {
        super(message, networkFeedback);
    }

    public PacketException(String message, Throwable cause, String networkFeedback) {
        super(message, cause, networkFeedback);
    }

    public PacketException(Throwable cause, String networkFeedback) {
        super(cause, networkFeedback);
    }
}
