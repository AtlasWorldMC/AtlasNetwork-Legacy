package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.api.networking.PacketByteBuf;
import fr.atlasworld.network.networking.packet.PacketByteBufImpl;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Decodes and decrypts the received data
 */
public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private final EncryptionManager encryptionManager;

    public DecodeHandler(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf rawBuf = new PacketByteBufImpl((ByteBuf) msg);

        if (!this.encryptionManager.isEncrypted()) {
            String packet = rawBuf.readString();
            AtlasNetworkOld.logger.debug("{} -> AtlasNetwork | Packet: {} | Encrypted: {}", ctx.channel().remoteAddress(),
                    packet, false);

            rawBuf.readerIndex(0);
            this.encryptionManager.handleHandshake(ctx.channel(), rawBuf);
            rawBuf.release();
            return;
        }

        PacketByteBuf processedBuf = this.encryptionManager.decrypt(rawBuf);
        rawBuf.release(); //Free memory

        String packet = processedBuf.readString();
        AtlasNetworkOld.logger.debug("{} -> AtlasNetwork | Packet: {} | Encrypted: {}", ctx.channel().remoteAddress(),
                packet, true);

        processedBuf.readerIndex(0);
        super.channelRead(ctx, processedBuf);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        this.encryptionManager.initHandshake(ctx.channel());
        super.channelActive(ctx);
    }
}
