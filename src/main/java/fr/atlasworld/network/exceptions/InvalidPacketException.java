package fr.atlasworld.network.exceptions;

public class InvalidPacketException extends IllegalArgumentException {
    public InvalidPacketException(String s) {
        super(s);
    }
}
