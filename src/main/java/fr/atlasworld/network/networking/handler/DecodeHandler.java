package fr.atlasworld.network.networking.handler;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.securty.encryption.EncryptionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private final EncryptionManager encryptionManager;

    public DecodeHandler(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        PacketByteBuf rawBuf = new PacketByteBuf((ByteBuf) msg);

        if (!this.encryptionManager.isEncrypted()) {
            this.encryptionManager.handleHandshake(ctx.channel(), rawBuf);
            return;
        }

        PacketByteBuf processedBuf = this.encryptionManager.decrypt(rawBuf);
        super.channelRead(ctx, processedBuf);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        this.encryptionManager.initHandshake(ctx.channel());
        super.channelActive(ctx);
    }
}
