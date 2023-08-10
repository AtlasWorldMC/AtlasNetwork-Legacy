package fr.atlasworld.network.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class NetworkSecurityManager implements SecurityManager {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_LENGTH = 2048;

    private final KeyPair sessionKeyPair;

    public NetworkSecurityManager() throws NoSuchAlgorithmException {
        this.sessionKeyPair = this.generateKeyPair();
    }

    @Override
    public KeyPair getSecurityPair() {
        return sessionKeyPair;
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        generator.initialize(KEY_LENGTH);
        return generator.generateKeyPair();
    }
}
