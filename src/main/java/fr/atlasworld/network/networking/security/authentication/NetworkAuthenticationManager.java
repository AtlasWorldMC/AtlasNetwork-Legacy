package fr.atlasworld.network.networking.security.authentication;

import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.networking.auth.AuthenticationException;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.channel.Channel;

import java.util.UUID;

public class NetworkAuthenticationManager implements AuthenticationManager {
    private boolean authenticated;
    private final SecurityManager securityManager;
    private final DatabaseManager databaseManager;

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
    public void authenticate(Channel channel, PacketByteBuf buf) throws AuthenticationException {
        UUID authUuid = buf.readUuid();

        if (this.securityManager.isAuthProfileActive(authUuid)) {
            throw new AuthenticationException("Profile already in use!", NetworkErrors.PROFILE_ALREADY_INUSE);
        }

        try {
            if (!this.databaseManager.authProfileExists(authUuid)) {
                throw new AuthenticationException("Profile does not exist!", NetworkErrors.UNKNOWN_OR_MISSING_PROFILE);
            }
        } catch (DatabaseException e) {
            throw new AuthenticationException("Cannot fetch authentication profile", NetworkErrors.INTERNAL_EXCEPTION, e);
        }

        try {
            AuthProfile profile = this.databaseManager.getAuthProfile(authUuid);
            String token = buf.readString();
            String hashedToken = this.securityManager.hash(token);

            if (!profile.tokenHash().equals(hashedToken)) {
                throw new AuthenticationException("Invalid token!", NetworkErrors.INVALID_TOKEN);
            }

            this.authenticated = true;
            this.securityManager.activateAuthProfile(authUuid);
            channel.closeFuture().addListener(future -> this.securityManager.deactivateAuthProfile(authUuid));
        } catch (DatabaseException e) {
            throw new AuthenticationException("Cannot fetch authentication profile", NetworkErrors.INTERNAL_EXCEPTION, e);
        }
    }
}
