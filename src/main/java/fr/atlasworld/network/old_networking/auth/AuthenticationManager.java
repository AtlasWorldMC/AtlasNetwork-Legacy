package fr.atlasworld.network.old_networking.auth;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;

public interface AuthenticationManager {
    AuthResult authenticate(Channel channel, PacketByteBuf buf);

}
