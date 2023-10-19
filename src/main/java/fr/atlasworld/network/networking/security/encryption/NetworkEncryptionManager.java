package fr.atlasworld.network.networking.security.encryption;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.encryption.exceptions.EncryptionException;
import fr.atlasworld.network.networking.security.encryption.exceptions.InternalEncryptionException;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import java.security.GeneralSecurityException;

/**
 * Network implementation of the EncryptionManager interface
 * @see EncryptionManager
 */
public class NetworkEncryptionManager implements EncryptionManager {
    private final SecurityManager securityManager;

    private Cipher encryptionCipher;
    private Cipher decryptionCipher;
    private boolean encryptionEnabled;

    public NetworkEncryptionManager(SecurityManager securityManager) {
        this.encryptionEnabled = false;
        this.securityManager = securityManager;
    }

    @Override
    public boolean isEncryptionEnabled() {
        return this.encryptionEnabled;
    }

    @Override
    public void enableEncryption(@NotNull SecretKey AESKey) throws EncryptionException {
        try {
            this.encryptionCipher = Cipher.getInstance("AES");
            this.encryptionCipher.init(Cipher.ENCRYPT_MODE, AESKey);

            AtlasNetwork.logger.debug("Encryption Cipher initialized.");

            this.decryptionCipher = Cipher.getInstance("AES");
            this.decryptionCipher.init(Cipher.DECRYPT_MODE, AESKey);

            AtlasNetwork.logger.debug("Decryption Cipher initialized.");

            this.encryptionEnabled = true;
        } catch (GeneralSecurityException e) {
            throw new InternalEncryptionException("Could not enable encryption", e);
        }
    }

    @Override
    public PacketByteBuf encrypt(PacketByteBuf buf) throws EncryptionException {
        if (!this.encryptionEnabled) {
            throw new UnsupportedOperationException("Encrypt/Decrypt methods can only be called once the encryption is enabled.");
        }

        try {
            byte[] encryptedData = this.encryptionCipher.doFinal(buf.asByteArray());
            return PacketByteBuf.fromBytes(encryptedData, buf.alloc());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new InternalEncryptionException("Could not encrypt data", e);
        }
    }

    @Override
    public PacketByteBuf decrypt(PacketByteBuf buf) throws EncryptionException {
        if (!this.encryptionEnabled) {
            throw new UnsupportedOperationException("Encrypt/Decrypt methods can only be called once the encryption is enabled.");
        }

        try {
            byte[] decryptedData = this.decryptionCipher.doFinal(buf.asByteArray());
            return PacketByteBuf.fromBytes(decryptedData, buf.alloc());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new InternalEncryptionException("Could not encrypt data", e);
        }
    }

    @Override
    public ChannelFuture sendPublicKey(Channel channel) {
        PacketByteBuf packet = new PacketByteBuf(channel.alloc().buffer());
        packet.writeBytes(this.securityManager.getSecurityPair().getPublic().getEncoded());

        return channel.writeAndFlush(packet); // send the key
    }

    @Override
    public byte[] decryptWithPrivateKey(byte[] bytes) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(this.securityManager.getSecurityPair().getPrivate().getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, this.securityManager.getSecurityPair().getPrivate());

            return cipher.doFinal(bytes);
        } catch (GeneralSecurityException e) {
            throw new InternalEncryptionException("Failed to decrypt data with private key.", e);
        }
    }
}
