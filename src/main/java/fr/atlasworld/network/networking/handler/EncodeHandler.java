package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.exceptions.InvalidPacketException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.securty.encryption.EncryptionManager;
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
            promise.setFailure(new InvalidPacketException("Only PacketByteBuf can be sent!"));
        }
    }
}
