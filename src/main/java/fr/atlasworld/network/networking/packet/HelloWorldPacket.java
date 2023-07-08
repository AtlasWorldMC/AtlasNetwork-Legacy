package fr.atlasworld.network.networking.packet;

import com.mongodb.client.MongoClient;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.internal.InternalRank;
import fr.atlasworld.network.networking.Client;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.PacketByteBuf;
import io.netty.buffer.Unpooled;

public class HelloWorldPacket implements NetworkPacket{
    @Override
    public String getIdentifier() {
        return "hello_world";
    }

    @Override
    public void execute(Client client, PacketByteBuf data) {
        MongoClient databaseClient = DatabaseManager.getManager().getClient();
        InternalRank clientRank = InternalRank.getRankById(client.getUser().getRankId(), databaseClient);

        PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());

        if (clientRank.hasPermission("hello.world")) {
            AtlasNetwork.logger.info("Received hello world packet from {}! Everything is working fine!",
                    client.getChannel().remoteAddress());

            response.writeString(this.getIdentifier() + "response");
        } else {
            AtlasNetwork.logger.warn("{} tried to sent '{}' packet without permission!'",
                    client.getChannel().remoteAddress(), this.getIdentifier());

            response.writeString("request_fail")
                    .writeString(NetworkErrors.NO_PERMISSION);
        }

        client.sendPacket(response).addListener(future -> {
            //Clean up
            data.release();
            response.release();
        });
    }
}
