package fr.atlasworld.network.security;

import java.security.KeyPair;
import java.util.UUID;

public interface SecurityManager {
    KeyPair getSecurityPair();
    String generateAuthenticationToken();
    String hash(String str);
    boolean isAuthProfileActive(UUID profileId);
    void activateAuthProfile(UUID profileId);
    void deactivateAuthProfile(UUID profileId);
}
