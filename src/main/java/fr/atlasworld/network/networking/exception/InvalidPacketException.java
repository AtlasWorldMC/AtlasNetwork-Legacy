package fr.atlasworld.network.networking.exception;

public class InvalidPacketException extends IllegalArgumentException {
    public InvalidPacketException(String s) {
        super(s);
    }
}
