package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.entities.PanelServer;

import java.util.List;

/**
 * Packet received by applications that requires special data from network to properly initialize
 * <p>
 * 0 - Packet ID
 * <p>
 * 1 - Operation (bytes: 0xO1 - ADD | 0x02 - REMOVE | 0x03 - RESET)
 * <p>
 * 2 - List of Servers
 */
public class InitializePacket implements NetworkPacket {
    @Override
    public String getKey() {
        return "app_init";
    }

    @Override
    public void onReceive(NetworkClient client, PacketByteBuf packet) {
        String appType = packet.readString();
        packet.release();

        switch (appType) {
            case "atlas-proxy" -> this.initAtlasProxy(client);
        }
    }

    private void initAtlasProxy(NetworkClient client) {
        // Reset remote servers
        PacketByteBuf resetServerPacket = PacketByteBuf.create()
                .writeString("update_server")
                .writeByte((byte) 0x03);

        client.sendPacket(resetServerPacket).syncUninterruptibly();

        // Add all servers with the default server configuration
        PacketByteBuf addServerPacket = PacketByteBuf.create();

        ServerManager serverManager = AtlasNetwork.getServerManager();
        List<PanelServer> serverToSent = serverManager.getServers().stream()
                .filter(server -> server.getConfiguration().equals(serverManager.defaultServerConfiguration()))
                .toList();


        addServerPacket.writeString("update_server")
                .writeByte((byte) 0x01)
                .writeInt(serverToSent.size());

        serverToSent.forEach(server -> {
            addServerPacket.writeString(server.name()).writeString(server.address().toString());
        });

        client.sendPacket(addServerPacket).syncUninterruptibly();
    }
}
