package fr.atlasworld.network.networking.security;

import java.security.KeyPair;
import java.util.UUID;

/**
 * Security Manager, Handles the security keys, profiles, ect...
 */
public interface SecurityManager {
    /**
     * Retrieve the private-public key pair generated for this session
     * @return key pair
     */
    KeyPair getSecurityPair();

    /**
     * Generate a socket authentication token
     * @return authentication token
     */
    String generateAuthenticationToken();

    /**
     * hash a string with the chosen implemented algorithm and salt
     * @param str string to hash
     * @return hashed string
     */
    String hash(String str);

    /**
     * Checks if a profile is already inuse
     * @param profileId profile id
     * @return true if profile is being used
     */
    boolean isAuthProfileActive(UUID profileId);

    /**
     * Marks a profile as being used
     * @param profileId profile id
     */
    void activateAuthProfile(UUID profileId);

    /**
     * Marks a profiles as being unused
     * @param profileId profile id
     */
    void deactivateAuthProfile(UUID profileId);
}
