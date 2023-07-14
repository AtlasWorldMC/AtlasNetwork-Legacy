package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.entities.Client;
import io.netty.buffer.Unpooled;

public class HelloWorldPacket implements NetworkPacket {
    @Override
    public String getIdentifier() {
        return "hello_world";
    }

    @Override
    public void execute(Client client, PacketByteBuf data) {
        AtlasNetwork.logger.info("Recieved Hello World Packet {}", client.remoteAddress());
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer())
                .writeString(getIdentifier())
                .writeBoolean(true);
        client.sendPacket(buf);
        buf.release();
    }
}
