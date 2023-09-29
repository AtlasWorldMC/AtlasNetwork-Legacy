package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.exceptions.networking.InvalidPacketException;
import fr.atlasworld.network.api.networking.PacketByteBuf;
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
            if (packet.equals("request_fail")) {
                AtlasNetworkOld.logger.warn("Request failed for {}: {}", ctx.channel().remoteAddress(),
                        buf.readString());
            }

            buf.readerIndex(0);
            if (this.encryptionManager.isEncrypted()) {
                AtlasNetworkOld.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                        ctx.channel().remoteAddress(), packet, true);
                super.write(ctx, this.encryptionManager.encrypt(buf), promise);
                return;
            }
            AtlasNetworkOld.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                    ctx.channel().remoteAddress(), packet, false);
            super.write(ctx, buf.asByteBuf(), promise);
        } else {
            AtlasNetworkOld.logger.error("Only PacketByteBuf objets can be sent!");
            promise.setFailure(new InvalidPacketException("Only PacketByteBuf can be sent!"));
        }
    }
}
