package fr.atlasworld.network.networking.security.encryption;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.encryption.exceptions.EncryptionException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import javax.crypto.SecretKey;

/**
 * Manages the encryption of a connection
 */
public interface EncryptionManager {
    /**
     * Checks if the connection is encrypted, value can only be changed internally using the handleHandshake function
     * @return true if the connection is encrypted
     */
    boolean isEncryptionEnabled();

    /**
     * Enables encryption for this connection.
     * @throws EncryptionException if encryption could not be enabled.
     */
    void enableEncryption(SecretKey AESKey) throws EncryptionException;

    /**
     * Encrypts a data buffer with the AES key established with the client
     * @param buf data to encrypt
     * @return encrypted data buffer
     * @throws EncryptionException if the data could not be encrypted
     */
    PacketByteBuf encrypt(PacketByteBuf buf) throws EncryptionException;

    /**
     * Decrypts a data buffer with the AES key established with the client
     * @param buf data to decrypt
     * @return decrypted data buffer
     * @throws EncryptionException if the data could not be decrypted
     */
    PacketByteBuf decrypt(PacketByteBuf buf) throws EncryptionException;

    /**
     * Sends the public key to the specified connection.
     * @return future operation of the sent data.
     */
    @CanIgnoreReturnValue
    ChannelFuture sendPublicKey(Channel channel);

    /**
     * Decrypts bytes encrypted with the public key, using the private key.
     * @return decrypted bytes.
     */
    byte[] decryptWithPrivateKey(byte[] bytes) throws EncryptionException;
}
