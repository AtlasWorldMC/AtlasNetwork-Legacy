package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.exceptions.request.UnknownRequestException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Decodes and decrypts the received data
 */
public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private final EncryptionManager encryptionManager;
    private long encryptionStartTime;

    public DecodeHandler(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buffer = new PacketByteBuf((ByteBuf) msg);

        if (this.encryptionManager.isEncryptionEnabled()) {
            PacketByteBuf decryptedBuffer = this.encryptionManager.decrypt(buffer);

            buffer.releaseFully();

            AtlasNetwork.logger.debug("{} -> AtlasNetwork | Packet: {} | Encrypted: {}",
                    ctx.channel().remoteAddress(),
                    decryptedBuffer.readString(),
                    true);

            decryptedBuffer.clear();
            super.channelRead(ctx, decryptedBuffer);
            return;
        }

        String packet = buffer.readString();

        if (packet.equals("handshake")) {
            String algorithm = buffer.readString();
            byte[] keyBytes = new byte[buffer.readInt()];

            buffer.readBytes(keyBytes);
            keyBytes = this.encryptionManager.decryptWithPrivateKey(keyBytes);

            buffer.releaseFully();

            SecretKey AESKey = new SecretKeySpec(keyBytes, algorithm);
            this.encryptionManager.enableEncryption(AESKey);
            long encryptionTime = (System.currentTimeMillis() - this.encryptionStartTime);

            AtlasNetwork.logger.info("Encryption established with '{}'!", ctx.channel().remoteAddress());

            PacketByteBuf response = new PacketByteBuf(ctx.channel().alloc().buffer())
                    .writeString("handshake")
                    .writeBoolean(true);

            ctx.channel().writeAndFlush(response);

            if (encryptionTime > 2000) {
                AtlasNetwork.logger.warn("Encryption took over 2000ms to complete (Network ping could be the cause?). Encryption took {}ms to complete.", encryptionTime);
            } else {
                AtlasNetwork.logger.debug("Encryption took {}ms to complete", encryptionTime);
            }
        } else {
            throw new UnknownRequestException("Has sent an unknown encryption request '" + packet + "'(Client up to date?)");
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        AtlasNetwork.logger.info("{} is connecting..", ctx.channel().remoteAddress());
        AtlasNetwork.logger.info("Starting encryption handshake with {}..", ctx.channel().remoteAddress());

        this.encryptionStartTime = System.currentTimeMillis();
        this.encryptionManager.sendPublicKey(ctx.channel());

        super.channelActive(ctx);
    }
}
