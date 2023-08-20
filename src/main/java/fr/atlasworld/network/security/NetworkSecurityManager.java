package fr.atlasworld.network.security;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.Config;

import java.security.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NetworkSecurityManager implements SecurityManager {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_LENGTH = 2048;
    private static final int TOKEN_LENGTH = 24;
    private static final String HASHING_ALGORITHM = "sha256";

    private final KeyPair sessionKeyPair;
    private final String hashSalt;
    private final Set<UUID> activeProfiles;

    public NetworkSecurityManager(Config config) throws NoSuchAlgorithmException {
        this.sessionKeyPair = this.generateKeyPair();
        this.hashSalt = config.getHashSalt();
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
        try {
            MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
            String saltedStr = this.hashSalt + str;
            byte[] bytes = md.digest(saltedStr.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            AtlasNetwork.logger.error("Unable to hash token", e);
        }
        return null;
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
