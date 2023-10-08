package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.api.exception.networking.packet.InvalidPacketException;
import fr.atlasworld.network.networking.packet.PacketByteBufImpl;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
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
        if (msg instanceof PacketByteBufImpl buf) {
            String packet = buf.readString();
            buf.clear();

            if (this.encryptionManager.isEncrypted()) {
                AtlasNetwork.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                        ctx.channel().remoteAddress(), packet, true);
                super.write(ctx, ((PacketByteBufImpl) this.encryptionManager.encrypt(buf)).asByteBuf(), promise);
                return;
            }

            AtlasNetwork.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                    ctx.channel().remoteAddress(), packet, false);
            super.write(ctx, buf.asByteBuf(), promise);
        } else {
            promise.setFailure(new InvalidPacketException("Only PacketByteBuf can be sent!"));
        }
    }
}
