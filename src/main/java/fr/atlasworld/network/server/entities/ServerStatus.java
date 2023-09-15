package fr.atlasworld.network.server.entities;

/**
 * Server Status
 */
public enum ServerStatus {
    /**
     * Server is installing
     */
    INSTALLING,

    /**
     * Server is offline
     */
    OFFLINE,

    /**
     * Server is starting
     */
    STARTING,

    /**
     * Server is online
     */
    ONLINE,

    /**
     * Server is stopping
     */
    STOPPING,

    /**
     * Server is suspended
     */
    SUSPENDED,

    /**
     * Server is corrupted, can be caused by a broken file or a failed installation
     */
    CORRUPTED;
}
