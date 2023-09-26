package fr.atlasworld.network.api.exception.module;

public class ModuleInvalidException extends ModuleException {
    public ModuleInvalidException() {
        super();
    }

    public ModuleInvalidException(String message) {
        super(message);
    }

    public ModuleInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleInvalidException(Throwable cause) {
        super(cause);
    }
}
