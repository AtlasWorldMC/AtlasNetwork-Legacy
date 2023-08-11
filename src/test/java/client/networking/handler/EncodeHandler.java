package client.networking.handler;

import client.NetworkClient;
import client.networking.ClientEncryptionManager;
import fr.atlasworld.network.exceptions.InvalidPacketException;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EncodeHandler extends ChannelOutboundHandlerAdapter {
    private final ClientEncryptionManager encryptionManager;

    public EncodeHandler(ClientEncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof PacketByteBuf buf) {
            String packet = buf.readString();
            buf.readerIndex(0);
            if (this.encryptionManager.isEncrypted()) {
                NetworkClient.logger.debug("Client -> AtlasNetwork | Packet: {} | Encrypted: {}", packet, true);
                super.write(ctx, this.encryptionManager.encrypt(buf), promise);
                return;
            }
            NetworkClient.logger.debug("Client -> AtlasNetwork | Packet: {} | Encrypted: {}", packet, false);
            super.write(ctx, buf.getParent(), promise);
        } else {
            promise.setFailure(new InvalidPacketException("Only PacketByteBuf can be sent!"));
        }
    }
}
