package fr.atlasworld.network.networking.securty.authentication;

import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.channel.Channel;

import java.util.UUID;

public class NetworkAuthenticationManager implements AuthenticationManager {
    private boolean authenticated;
    private SecurityManager securityManager;
    private DatabaseManager databaseManager;

    public NetworkAuthenticationManager(SecurityManager securityManager, DatabaseManager databaseManager) {
        this.authenticated = false;
        this.securityManager = securityManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public AuthenticationResult authenticate(Channel channel, PacketByteBuf buf) {
        UUID authUuid = buf.readUuid();

        if (this.securityManager.isAuthProfileActive(authUuid)) {
            return new AuthenticationResult(false, NetworkErrors.PROFILE_ALREADY_INUSE);
        }

        if (!this.databaseManager.authProfileExists(authUuid)) {
            return new AuthenticationResult(false, NetworkErrors.UNKNOWN_OR_MISSING_PROFILE);
        }

        AuthProfile profile = this.databaseManager.getAuthProfile(authUuid);
        String token = buf.readString();
        String hashedToken = this.securityManager.hash(token);

        if (!profile.tokenHash().equals(hashedToken)) {
            return new AuthenticationResult(false, NetworkErrors.INVALID_TOKEN);
        }

        this.authenticated = true;
        this.securityManager.activateAuthProfile(authUuid);
        return new AuthenticationResult(true, "success");
    }
}
