package fr.atlasworld.network.security;

import java.security.KeyPair;

public interface SecurityManager {
    KeyPair getSecurityPair();
    String generateAuthenticationToken();
    String hash(String str);
}
