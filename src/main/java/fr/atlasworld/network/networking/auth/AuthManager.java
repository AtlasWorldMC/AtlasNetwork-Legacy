package fr.atlasworld.network.networking.auth;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.auth.type.AuthType;
import fr.atlasworld.network.networking.auth.type.ServiceAuthType;
import io.netty.channel.Channel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

public class AuthManager {
    public static final int TOKEN_LENGTH = 20;

    private final HashMap<Channel, String> tokenHolder;
    private final List<AuthType> authTypes;

    private AuthManager(HashMap<Channel, String> tokenHolder, List<AuthType> authTypes) {
        this.tokenHolder = tokenHolder;
        this.authTypes = authTypes;
    }

    public boolean validate(Channel channel, String token) {
        if (tokenHolder.containsKey(channel)) {
            String hashedToken = hashString(token, "SHA-256");

            return tokenHolder.get(channel).equals(hashedToken);
        }
        return false;
    }

    public AuthResult authenticate(Channel channel, int authType, PacketByteBuf buf) {
        if (this.authTypes.size() < authType - 1) {
            AtlasNetwork.logger.error("{} requested an unknown authentication method; id: {}", channel.remoteAddress(), authType);
            return new AuthResult(false, "UNKNOWN_AUTHENTICATION_METHOD", "", "");
        }

        AuthType.AuthTypeResults result = this.authTypes.get(authType).authenticate(buf);

        //Auth failed
        if (!result.successful()) {
            AtlasNetwork.logger.warn("Auth failed for {}, {}", channel.remoteAddress(), result.message());
            return new AuthResult(result.successful(), result.message(), "", "");
        }

        AtlasNetwork.logger.info("{} connected!", channel.remoteAddress());
        AtlasNetwork.logger.info("Auth successful for {}!", channel.remoteAddress());

        String token = generateSecureToken(TOKEN_LENGTH);
        String hashedToken = hashString(token, "SHA-256");

        this.tokenHolder.put(channel, hashedToken);

        return new AuthResult(result.successful(), result.message(), token, result.userId());
    }

    public void invalidateToken(Channel channel) {
        this.tokenHolder.remove(channel);
    }

    //Static fields
    public static AuthManager manager;

    public static AuthManager getManager() {
        if (manager == null) {
            return new AuthManager(new HashMap<>(), List.of(new ServiceAuthType(DatabaseManager.getManager().getClient())));
        }

        return manager;
    }

    public static String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes.toString();
    }

    public static String hashString(String string, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(string.getBytes());

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            AtlasNetwork.logger.error("Unable to hash token", e);
        }
        return null;
    }
}
