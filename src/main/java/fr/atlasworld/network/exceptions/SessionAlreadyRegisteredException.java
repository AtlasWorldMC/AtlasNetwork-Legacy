package fr.atlasworld.network.exceptions;

public class SessionAlreadyRegisteredException extends IllegalStateException {
    public SessionAlreadyRegisteredException(String s) {
        super(s);
    }
}
