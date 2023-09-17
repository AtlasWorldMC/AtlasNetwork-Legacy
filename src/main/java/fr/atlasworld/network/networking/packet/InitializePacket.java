package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.entities.PanelServer;

import java.util.List;

/**
 * Packet received by applications that requires special data from network to properly initialize
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
        ServerManager serverManager = AtlasNetwork.getServerManager();
        List<PanelServer> serverToSent = serverManager.getServers().stream()
                .filter(server -> !server.getConfiguration().equals(serverManager.defaultProxyConfiguration()))
                .toList();

        PacketByteBuf syncServersPacket = PacketByteBuf.create()
                .writeString("update_servers")
                .writeByte((byte) 0x03)
                .writeInt(serverToSent.size());

        serverToSent.forEach(server -> {
            syncServersPacket
                    .writeString(server.name())
                    .writeString(server.getConfiguration().id())
                    .writeString(server.address().getHostString())
                    .writeInt(server.address().getPort());
        });

        client.sendPacket(syncServersPacket);
    }
}
