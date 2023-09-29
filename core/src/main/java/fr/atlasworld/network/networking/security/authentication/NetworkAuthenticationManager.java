package fr.atlasworld.network.networking.security.authentication;

import fr.atlasworld.network.api.networking.PacketByteBuf;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.networking.auth.AuthenticationException;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.channel.Channel;

import java.util.UUID;

/**
 * Network implementation of the Authentication Manager
 * @see AuthenticationManager
 */
public class NetworkAuthenticationManager implements AuthenticationManager {
    private boolean authenticated;
    private final SecurityManager securityManager;
    private final Database<AuthenticationProfile> database;

    public NetworkAuthenticationManager(SecurityManager securityManager, Database<AuthenticationProfile> database) {
        this.authenticated = false;
        this.securityManager = securityManager;
        this.database = database;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public UUID authenticate(Channel channel, PacketByteBuf buf) throws AuthenticationException {
        UUID authUuid = UUID.fromString(buf.readString());

        if (this.securityManager.isAuthProfileActive(authUuid)) {
            throw new AuthenticationException("Profile already in use!", NetworkErrors.PROFILE_ALREADY_INUSE);
        }

        try {
            if (!this.database.has(authUuid.toString())) {
                throw new AuthenticationException("Profile does not exist!", NetworkErrors.UNKNOWN_OR_MISSING_PROFILE);
            }
        } catch (DatabaseException e) {
            throw new AuthenticationException("Cannot fetch authentication profile", NetworkErrors.INTERNAL_EXCEPTION, e);
        }

        try {
            AuthenticationProfile profile = this.database.get(authUuid.toString());
            String token = buf.readString();
            String hashedToken = this.securityManager.hash(token);

            if (!profile.hashedToken().equals(hashedToken)) {
                throw new AuthenticationException("Invalid token!", NetworkErrors.INVALID_TOKEN);
            }

            this.authenticated = true;
            this.securityManager.activateAuthProfile(authUuid);
            channel.closeFuture().addListener(future -> this.securityManager.deactivateAuthProfile(authUuid));
            return authUuid;
        } catch (DatabaseException e) {
            throw new AuthenticationException("Cannot fetch authentication profile", NetworkErrors.INTERNAL_EXCEPTION, e);
        }
    }
}
