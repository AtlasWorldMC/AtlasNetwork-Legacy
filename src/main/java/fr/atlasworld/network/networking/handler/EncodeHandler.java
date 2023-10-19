package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.packet.exceptions.PacketSendingException;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Encodes and encrypt data for sending
 */
public class EncodeHandler extends ChannelOutboundHandlerAdapter {
    private final EncryptionManager encryptionManager;

    public EncodeHandler(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE); // Makes the exceptionCaught() function work.

        if (!(msg instanceof ByteBuf)) {
            throw new PacketSendingException("Only ByteBuffers are allowed to be sent!");
        }

        // Get the buffer
        PacketByteBuf buffer;
        if (msg instanceof PacketByteBuf pBuf) {
            buffer = pBuf;

        } else {
            buffer = new PacketByteBuf((ByteBuf) msg);
            AtlasNetwork.logger.debug("Sending netty's ByteBuf, not recommended but it won't break anything.");
        }

        String packet = buffer.readString();
        buffer.clear();

        if (packet.equals("request_fail")) {
            AtlasNetwork.logger.warn("Failed to fulfill request for {}: {}", ctx.channel().remoteAddress(),
                    buffer.readString());
        }

        if (this.encryptionManager.isEncryptionEnabled()) {
            AtlasNetwork.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                    ctx.channel().remoteAddress(), packet, true);

            PacketByteBuf encryptedBuffer = this.encryptionManager.encrypt(buffer);
            buffer.releaseFully();

            super.write(ctx, encryptedBuffer.asByteBuf(), promise);
            return;
        }

        AtlasNetwork.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                ctx.channel().remoteAddress(), packet, false);
        super.write(ctx, buffer.asByteBuf(), promise);
    }
}
