package fr.atlasworld.network.api.exception.module;

/**
 * Thrown when something related to the module class loading fails.
 */
public class ModuleInvalidClassException extends ModuleInvalidException {
    public ModuleInvalidClassException() {
        super();
    }

    public ModuleInvalidClassException(String message) {
        super(message);
    }

    public ModuleInvalidClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleInvalidClassException(Throwable cause) {
        super(cause);
    }
}
