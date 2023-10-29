package fr.atlasworld.network.services;

import fr.atlasworld.network.services.exception.ServiceException;

/**
 * Represents a service
 */
public interface Service {
    /**
     * Starts up the service
     */
    void start() throws ServiceException;

    /**
     * Stops the service
     */
    void stop() throws ServiceException;
}
