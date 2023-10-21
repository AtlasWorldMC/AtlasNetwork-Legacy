package fr.atlasworld.network.networking.security.authentication;

import fr.atlasworld.network.networking.entities.data.AuthenticationProfile;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.authentication.exceptions.AuthenticationException;
import fr.atlasworld.network.networking.security.authentication.exceptions.InternalAuthenticationException;
import fr.atlasworld.network.networking.security.authentication.exceptions.ProfileAlreadyUsedException;
import fr.atlasworld.network.networking.security.authentication.exceptions.InvalidAuthenticationCredentials;
import fr.atlasworld.network.security.SecurityManager;
import fr.atlasworld.network.services.database.Database;
import fr.atlasworld.network.services.database.exceptions.DatabaseException;
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
            throw new ProfileAlreadyUsedException("Profile(" + authUuid + ") is already connected!");
        }

        try {
            AuthenticationProfile profile = this.database.load(authUuid);
            if (profile == null) {
                throw new InvalidAuthenticationCredentials("Profile(" + authUuid + ") does not exist!");
            }

            String token = buf.readString();
            String hashedToken = this.securityManager.hash(token);

            if (!profile.tokenHash().equals(hashedToken)) {
                throw new InvalidAuthenticationCredentials("Provided invalid token for profile(" + authUuid + ")");
            }

            this.authenticated = true;
            this.securityManager.activateAuthProfile(authUuid);
            channel.closeFuture().addListener(future -> this.securityManager.deactivateAuthProfile(authUuid));
            return authUuid;
        } catch (DatabaseException e) {
            throw new InternalAuthenticationException("Could not fetch database", e);
        }
    }
}
