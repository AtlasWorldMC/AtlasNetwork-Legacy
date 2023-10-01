package fr.atlasworld.network.networking.security.encryption;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.security.GeneralSecurityException;

/**
 * Manages the encryption of a connection
 */
public interface EncryptionManager {
    /**
     * Checks if the connection is encrypted, value can only be changed internally using the handleHandshake function
     * @return true if the connection is encrypted
     */
    boolean isEncrypted();

    /**
     * Handle the handshake request of the client, should normally be invoked after that the client has received
     * the packet of initHandshake function
     * @param channel channel to encrypt
     * @param buf request data
     * @throws GeneralSecurityException if the encryption could not be established
     */
    void handleHandshake(Channel channel, PacketByteBuf buf) throws GeneralSecurityException;

    /**
     * Initializes the encryption handshake, should be executed before anything else when starting encryption
     * @param channel channel to encrypt
     */
    void initHandshake(Channel channel);

    /**
     * Encrypts a data buffer with the AES key established with the client
     * @param buf data to encrypt
     * @return encrypted data buffer
     * @throws GeneralSecurityException if the data could not be encrypted
     */
    ByteBuf encrypt(PacketByteBuf buf) throws GeneralSecurityException;

    /**
     * Decrypts a data buffer with the AES key established with the client
     * @param buf data to decrypt
     * @return decrypted data buffer
     * @throws GeneralSecurityException if the data could not be decrypted
     */
    PacketByteBuf decrypt(PacketByteBuf buf) throws GeneralSecurityException;
}
