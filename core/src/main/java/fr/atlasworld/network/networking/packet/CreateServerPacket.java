package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.configuration.ServerConfiguration;

import java.util.UUID;

public class CreateServerPacket implements NetworkPacket {
    private final ServerManager serverManager;

    public CreateServerPacket(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public String getKey() {
        return "create_server";
    }

    @Override
    public void onReceive(NetworkClient client, PacketByteBuf packet) {
        AtlasNetworkOld.logger.info("Create server request received!");
        byte action = packet.readByte();
        switch (action) {
            case 0x01 -> this.createProxy(packet, client);
            case 0x02 -> this.createServer(packet, client);
            case 0x03 -> this.createCustom(packet, client);
        }

        packet.release();
    }

    private void createProxy(PacketByteBuf buf, NetworkClient client) {
        try {
            this.serverManager.createDefaultProxy(UUID.fromString(buf.readString()));
        } catch (PanelException e) {
            this.respondFailure(client, e);
        }
    }

    private void createServer(PacketByteBuf buf, NetworkClient client) {
        try {
            this.serverManager.createDefaultServer(UUID.fromString(buf.readString()));
        } catch (Exception e) {
            this.respondFailure(client, e);
        }
    }

    private void createCustom(PacketByteBuf buf, NetworkClient client) {
        UUID requestId = UUID.fromString(buf.readString());
        String name = buf.readString();
        String configId = buf.readString();

        ServerConfiguration configuration = this.serverManager.getConfiguration(configId);
        if (configuration == null) {
            AtlasNetworkOld.logger.warn("Unknown server configuration: {}", configId);
            PacketByteBuf response = new PacketByteBufImpl(client.getChannel().alloc().buffer())
                    .writeString("request_fail")
                    .writeString("create_server")
                    .writeString(requestId.toString())
                    .writeString("UNKNOWN_SERVER_CONFIGURATION");

            client.sendPacket(response);
        }

        try {
            this.serverManager.createCustomServer(configuration, name, requestId);
        } catch (PanelException e) {
            this.respondFailure(client, e);
        }
    }

    private void respondFailure(NetworkClient client, Exception e) {
        AtlasNetworkOld.logger.error("Could not fulfill server creation request!", e);
        PacketByteBuf failureResponse = new PacketByteBufImpl(client.getChannel().alloc().buffer())
                .writeString("request_fail")
                .writeString(NetworkErrors.INTERNAL_EXCEPTION);

        client.sendPacket(failureResponse);
    }
}
