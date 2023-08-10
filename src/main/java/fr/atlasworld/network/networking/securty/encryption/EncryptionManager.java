package fr.atlasworld.network.networking.securty.encryption;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.security.GeneralSecurityException;

public interface EncryptionManager {
    boolean isEncrypted();
    void handleHandshake(Channel channel, PacketByteBuf buf) throws GeneralSecurityException;
    void initHandshake(Channel channel);
    ByteBuf encrypt(PacketByteBuf buf) throws GeneralSecurityException;
    PacketByteBuf decrypt(PacketByteBuf buf) throws GeneralSecurityException;
}
