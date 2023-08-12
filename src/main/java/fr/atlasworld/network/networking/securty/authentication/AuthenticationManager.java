package fr.atlasworld.network.networking.securty.authentication;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;

public interface AuthenticationManager {
    boolean isAuthenticated();
    AuthenticationResult authenticate(Channel channel, PacketByteBuf buf);
}
