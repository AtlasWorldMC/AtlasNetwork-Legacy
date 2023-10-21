package fr.atlasworld.network.services.exception.unchecked;

import fr.atlasworld.network.services.exception.ServiceException;

import java.io.UncheckedIOException;

public class UncheckedServiceException extends UncheckedIOException {
    public UncheckedServiceException(String message, ServiceException cause) {
        super(message, cause);
    }

    public UncheckedServiceException(ServiceException cause) {
        super(cause);
    }
}
