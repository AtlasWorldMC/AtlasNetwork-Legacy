package client.networking.handler;

import client.networking.ClientEncryptionManager;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.securty.encryption.EncryptionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private final ClientEncryptionManager encryptionManager;

    public DecodeHandler(ClientEncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf buf = new PacketByteBuf((ByteBuf) msg);

        if (!this.encryptionManager.isEncrypted()) {
            this.encryptionManager.handleHandshake(ctx.channel(), buf);
            return;
        }

        PacketByteBuf processedBuffer = this.encryptionManager.decrypt(buf);
        super.channelRead(ctx, processedBuffer);
    }
}
