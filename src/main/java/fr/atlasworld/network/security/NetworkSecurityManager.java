package fr.atlasworld.network.security;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.files.SecurityConfiguration;

import java.security.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Network implementation of the SecurityManager, uses RSA for the key pair, and SHA-256 for hashing
 * <p>
 * RaftDev Comment: I hate encryption
 * @see SecurityManager
 */
public class NetworkSecurityManager implements SecurityManager {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_LENGTH = 2048;
    private static final int TOKEN_LENGTH = 24;
    private static final String HASHING_ALGORITHM = "sha256";

    private final KeyPair sessionKeyPair;
    private final String hashSalt;
    private final Set<UUID> activeProfiles;

    public NetworkSecurityManager(SecurityConfiguration config) throws NoSuchAlgorithmException {
        this.sessionKeyPair = this.generateKeyPair();
        this.hashSalt = config.hashSalt();
        this.activeProfiles = new HashSet<>();
    }

    @Override
    public KeyPair getSecurityPair() {
        return sessionKeyPair;
    }

    @Override
    public String generateAuthenticationToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    @Override
    public String hash(String str) {
        return Base64.getEncoder().encodeToString(SecurityUtilities.hash(str.toCharArray(), Base64.getDecoder().decode(this.hashSalt)));
    }

    @Override
    public boolean isAuthProfileActive(UUID profileId) {
        return this.activeProfiles.contains(profileId);
    }

    @Override
    public void activateAuthProfile(UUID profileId) {
        this.activeProfiles.add(profileId);
    }

    @Override
    public void deactivateAuthProfile(UUID profileId) {
        this.activeProfiles.remove(profileId);
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        generator.initialize(KEY_LENGTH);
        return generator.generateKeyPair();
    }
}
