package fr.atlasworld.network.networking.security.encryption;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.packet.PacketByteBufImpl;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.channel.Channel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * Network implementation of the EncryptionManager interface
 * @see EncryptionManager
 */
public class NetworkEncryptionManager implements EncryptionManager {
    private boolean encrypted;
    private final SecurityManager securityManager;

    private Cipher decryptionCipher;
    private Cipher encryptionCipher;

    public NetworkEncryptionManager(SecurityManager securityManager){
        this.encrypted = false;
        this.securityManager = securityManager;
    }

    @Override
    public boolean isEncrypted() {
        return this.encrypted;
    }

    @Override
    public void handleHandshake(Channel channel, PacketByteBuf buf) throws GeneralSecurityException {
        if (buf.readString().equals("encryption_handshake")) {
            String algorithm = buf.readString();
            byte[] keyBytes = new byte[buf.readInt()];
            buf.readBytes(keyBytes);

            byte[] decryptedBytes = this.decryptKey(keyBytes);
            SecretKey AESSecretKey = new SecretKeySpec(decryptedBytes, algorithm);

            this.encryptionCipher = Cipher.getInstance("AES");
            this.encryptionCipher.init(Cipher.ENCRYPT_MODE, AESSecretKey);

            this.decryptionCipher = Cipher.getInstance("AES");
            this.decryptionCipher.init(Cipher.DECRYPT_MODE, AESSecretKey);

            //Encryption was successful
            this.encrypted = true;
            AtlasNetwork.logger.info("Encryption established with {}", channel.remoteAddress());

            PacketByteBuf respBuf = new PacketByteBufImpl(channel.alloc().buffer())
                    .writeString("encryption_handshake")
                    .writeBoolean(true);
            channel.writeAndFlush(respBuf);
            return;
        }

        PacketByteBuf response = new PacketByteBufImpl(channel.alloc().buffer())
                .writeString("request_fail")
                .writeString("ENCRYPTION_HANDSHAKE_FAIL");

        AtlasNetwork.logger.error("Encryption failed with {}, terminating connection..", channel.remoteAddress());

        channel.writeAndFlush(response);
        channel.disconnect();
    }

    @Override
    public void initHandshake(Channel channel) {
        PacketByteBuf buf = new PacketByteBufImpl(channel.alloc().buffer())
                .writeString("public_key");
        buf.writeBytes(this.securityManager.getSecurityPair().getPublic().getEncoded());

        AtlasNetwork.logger.info("Starting handshake with {}", channel.remoteAddress());

        channel.writeAndFlush(buf);
    }

    public byte[] decryptKey(byte[] bytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(this.securityManager.getSecurityPair().getPrivate().getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, this.securityManager.getSecurityPair().getPrivate());

        return cipher.doFinal(bytes);
    }

    @Override
    public PacketByteBuf encrypt(PacketByteBuf buf) throws GeneralSecurityException {
        byte[] inputBytes;

        if (buf.hasArray()) {
            inputBytes = buf.array();
        } else {
            buf.clear();
            inputBytes = new byte[buf.readableBytes()];
            buf.getBytes(0, inputBytes);
        }

        byte[] encryptedBytes = this.encryptionCipher.doFinal(inputBytes);
        return buf.clear().writeBytes(encryptedBytes);
    }

    @Override
    public PacketByteBuf decrypt(PacketByteBuf buf) throws GeneralSecurityException {
        byte[] inputBytes;

        if (buf.hasArray()) {
            inputBytes = buf.array();
        } else {
            buf.clear();
            inputBytes = new byte[buf.readableBytes()];
            buf.getBytes(0, inputBytes);
        }

        byte[] decryptedBytes = this.decryptionCipher.doFinal(inputBytes);
        return buf.clear().writeBytes(decryptedBytes);
    }
}
