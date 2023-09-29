package fr.atlasworld.network.networking.security.encryption;

import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.api.networking.PacketByteBuf;
import fr.atlasworld.network.networking.packet.PacketByteBufImpl;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
    private SecretKey AESSecretKey;

    public NetworkEncryptionManager(SecurityManager securityManager) {
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
            this.AESSecretKey = new SecretKeySpec(decryptedBytes, algorithm);

            //Encryption was successful
            this.encrypted = true;
            AtlasNetworkOld.logger.info("Encryption established with {}", channel.remoteAddress());

            PacketByteBuf respBuf = new PacketByteBufImpl(channel.alloc().buffer())
                    .writeString("encryption_handshake")
                    .writeBoolean(true);
            channel.writeAndFlush(respBuf);
            return;
        }

        PacketByteBuf response = new PacketByteBufImpl(channel.alloc().buffer())
                .writeString("request_fail")
                .writeString("ENCRYPTION_HANDSHAKE_FAIL");

        AtlasNetworkOld.logger.error("Encryption failed with {}, terminating connection..", channel.remoteAddress());

        channel.writeAndFlush(response);
        channel.disconnect();
    }

    @Override
    public void initHandshake(Channel channel) {
        PacketByteBuf buf = new PacketByteBufImpl(channel.alloc().buffer())
                .writeString("public_key");
        buf.writeBytes(this.securityManager.getSecurityPair().getPublic().getEncoded());

        AtlasNetworkOld.logger.info("Starting handshake with {}", channel.remoteAddress());

        channel.writeAndFlush(buf);
    }

    public byte[] decryptKey(byte[] bytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(this.securityManager.getSecurityPair().getPrivate().getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, this.securityManager.getSecurityPair().getPrivate());

        return cipher.doFinal(bytes);
    }

    @Override
    public ByteBuf encrypt(PacketByteBuf buf) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, AESSecretKey);

        byte[] inputBytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), inputBytes);

        byte[] encryptedBytes = cipher.doFinal(inputBytes);
        return Unpooled.wrappedBuffer(encryptedBytes);
    }

    @Override
    public PacketByteBuf decrypt(PacketByteBuf buf) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, AESSecretKey);

        byte[] inputBytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), inputBytes);

        byte[] decryptedBytes = cipher.doFinal(inputBytes);
        return new PacketByteBufImpl(Unpooled.wrappedBuffer(decryptedBytes));
    }
}
