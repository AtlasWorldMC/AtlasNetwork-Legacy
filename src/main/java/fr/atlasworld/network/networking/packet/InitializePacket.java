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
 * 1 - Operation (bytes: 0xO1 - ADD | 0x02 - REMOVE | 0x03 - SYNC)
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
        System.out.println("init proxy");
        try {
            ServerManager serverManager = AtlasNetwork.getServerManager();
            List<PanelServer> serverToSent = serverManager.getServers().stream()
                    .filter(server -> server.getConfiguration().equals(serverManager.defaultServerConfiguration()))
                    .toList();

            System.out.println("init proxy");
            PacketByteBuf syncServersPacket = PacketByteBuf.create()
                    .writeString("update_servers")
                    .writeByte((byte) 0x03)
                    .writeInt(serverToSent.size());

            System.out.println("init proxy");
            serverToSent.forEach(server -> {
                syncServersPacket
                        .writeString(server.name())
                        .writeString(server.address().getHostString())
                        .writeInt(server.address().getPort());
            });

            System.out.println("init proxy");
            client.sendPacket(syncServersPacket).addListener(future -> {
                if(!future.isSuccess()) {
                    AtlasNetwork.logger.error("Could not send init packet", future.cause());
                }
            }).syncUninterruptibly();
            System.out.println("Sent");
        } catch (Exception e) {
            AtlasNetwork.logger.error("e", e);
        }
    }
}
