package fr.atlasworld.network.networking.packet.exceptions;

public class PacketExecutionException extends PacketException {

    public PacketExecutionException(String message) {
        super(message);
    }

    public PacketExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketExecutionException(Throwable cause) {
        super(cause);
    }

    public String getFeedBack() {
        return "PACKET_EXECUTION_FAILED";
    }
}
