package client.networking;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class ClientEncryptionManager {
    private static final int KEY_LENGTH = 294;

    private boolean encrypted;
    private PublicKey serverKey;
    private SecretKey AESSecretKey;

    public ClientEncryptionManager() {
        this.encrypted = false;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public void handleHandshake(Channel channel, PacketByteBuf buf) throws GeneralSecurityException {
        if (buf.readString().equals("public_key")) {
            byte[] publicKeyBytes = new byte[KEY_LENGTH];
            buf.getParent().readBytes(publicKeyBytes);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            this.serverKey = factory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            AESSecretKey = this.generateAESKey();
            this.encrypted = true;

            byte[] encryptedKey = this.encryptKey(this.AESSecretKey.getEncoded());
            PacketByteBuf respBuf = PacketByteBuf.create()
                    .writeString("encryption_handshake")
                    .writeString(this.AESSecretKey.getAlgorithm())
                    .writeInt(encryptedKey.length);
            respBuf.getParent().writeBytes(encryptedKey);

            channel.writeAndFlush(respBuf.getParent());
            return;
        }
        System.out.println("Unknown packet");
    }

    public byte[] encryptKey(byte[] bytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(this.serverKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, this.serverKey);

        return cipher.doFinal(bytes);
    }


    public ByteBuf encrypt(PacketByteBuf buf) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, AESSecretKey);

        byte[] encryptedBytes = cipher.doFinal(buf.getParent().array());
        return Unpooled.wrappedBuffer(encryptedBytes);
    }


    public PacketByteBuf decrypt(PacketByteBuf buf) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, AESSecretKey);

        byte[] inputBytes;
        if (buf.getParent().hasArray() && buf.getParent().arrayOffset() == 0 && buf.getParent().readableBytes() == buf.capacity()) {
            // Use the backing array if available and accessible
            inputBytes = buf.getParent().array();
        } else {
            // If buf is a direct buffer or has a non-zero array offset, copy the data
            inputBytes = new byte[buf.getParent().readableBytes()];
            buf.getParent().getBytes(buf.getParent().readerIndex(), inputBytes);
        }

        byte[] decryptedBytes = cipher.doFinal(inputBytes);
        return new PacketByteBuf(Unpooled.wrappedBuffer(decryptedBytes));
    }

    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

}
