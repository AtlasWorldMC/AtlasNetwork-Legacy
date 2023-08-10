package fr.atlasworld.network.networking.securty.encryption;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.security.SecurityManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
            buf.getParent().readBytes(keyBytes);

            byte[] decryptedBytes = this.decryptKey(keyBytes);
            this.AESSecretKey = new SecretKeySpec(decryptedBytes, algorithm);

            //Encryption was successful
            this.encrypted = true;
            AtlasNetwork.logger.info("Encryption established with {}", channel.remoteAddress());

            PacketByteBuf respBuf = PacketByteBuf.create()
                    .writeString("encryption_handshake")
                    .writeBoolean(true);
            channel.writeAndFlush(respBuf);
        }
    }

    @Override
    public void initHandshake(Channel channel) {
        PacketByteBuf buf = PacketByteBuf.create()
                .writeString("public_key");
        buf.getParent().writeBytes(this.securityManager.getSecurityPair().getPublic().getEncoded());

        AtlasNetwork.logger.info("Starting handshake with {}", channel.remoteAddress());

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

        byte[] inputBytes = new byte[buf.getParent().readableBytes()];
        buf.getParent().getBytes(buf.getParent().readerIndex(), inputBytes);

        byte[] encryptedBytes = cipher.doFinal(inputBytes);
        return Unpooled.wrappedBuffer(encryptedBytes);
    }

    @Override
    public PacketByteBuf decrypt(PacketByteBuf buf) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, AESSecretKey);

        byte[] inputBytes = new byte[buf.getParent().readableBytes()];
        buf.getParent().getBytes(buf.getParent().readerIndex(), inputBytes);

        byte[] decryptedBytes = cipher.doFinal(inputBytes);
        return new PacketByteBuf(Unpooled.wrappedBuffer(decryptedBytes));
    }
}
