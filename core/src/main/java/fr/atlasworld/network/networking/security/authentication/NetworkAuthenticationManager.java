package fr.atlasworld.network.networking.security.authentication;

import fr.atlasworld.network.api.exception.networking.requests.InternalRequestFailureException;
import fr.atlasworld.network.api.exception.networking.requests.RequestFailException;
import fr.atlasworld.network.api.exception.networking.authentication.ProfileAlreadyInuseException;
import fr.atlasworld.network.api.exception.networking.authentication.AuthenticationFailedException;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.api.exception.networking.authentication.AuthenticationException;
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
    public UUID authenticate(Channel channel, PacketByteBuf buf) throws AuthenticationException, RequestFailException {
        UUID authUuid = UUID.fromString(buf.readString());

        if (this.securityManager.isAuthProfileActive(authUuid)) {
            throw new ProfileAlreadyInuseException();
        }

        try {
            if (!this.database.has(authUuid.toString())) {
                throw new AuthenticationFailedException("unknown profile request");
            }
        } catch (DatabaseException e) {
            throw new InternalRequestFailureException("Cannot fetch authentication profile", e);
        }

        try {
            AuthenticationProfile profile = this.database.get(authUuid.toString());
            String token = buf.readString();
            String hashedToken = this.securityManager.hash(token);

            if (!profile.hashedToken().equals(hashedToken)) {
                throw new AuthenticationFailedException("invalid profile token");
            }

            this.authenticated = true;
            this.securityManager.activateAuthProfile(authUuid);
            channel.closeFuture().addListener(future -> this.securityManager.deactivateAuthProfile(authUuid));
            return authUuid;
        } catch (DatabaseException e) {
            throw new InternalRequestFailureException("Cannot fetch authentication profile", e);
        }
    }
}
