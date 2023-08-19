package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.exceptions.InvalidPacketException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.security.encryption.EncryptionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EncodeHandler extends ChannelOutboundHandlerAdapter {
    private final EncryptionManager encryptionManager;

    public EncodeHandler(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof PacketByteBuf buf) {
            String packet = buf.readString();
            if (packet.equals("request_fail")) {
                AtlasNetwork.logger.warn("Request failed for {}: {}", ctx.channel().remoteAddress(),
                        buf.readString());
            }

            buf.readerIndex(0);
            if (this.encryptionManager.isEncrypted()) {
                AtlasNetwork.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                        ctx.channel().remoteAddress(), packet, true);
                super.write(ctx, this.encryptionManager.encrypt(buf), promise);
                return;
            }
            AtlasNetwork.logger.debug("AtlasNetwork -> {} | Packet: {} | Encrypted: {}",
                    ctx.channel().remoteAddress(), packet, false);
            super.write(ctx, buf.getParent(), promise);
        } else {
            AtlasNetwork.logger.error("Only PacketByteBuf objets can be sent!");
            promise.setFailure(new InvalidPacketException("Only PacketByteBuf can be sent!"));
        }
    }
}
