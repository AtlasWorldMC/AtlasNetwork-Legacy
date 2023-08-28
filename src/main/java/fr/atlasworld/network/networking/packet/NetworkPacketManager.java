package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.entities.NetworkClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkPacketManager implements PacketManager {
    private final ExecutorService executor;
    private final Map<String, NetworkPacket> packets;

    public NetworkPacketManager(ExecutorService executor, Map<String, NetworkPacket> packets) {
        this.executor = executor;
        this.packets = packets;
    }

    public NetworkPacketManager() {
        this(Executors.newFixedThreadPool(32), new HashMap<>());
    }

    @Override
    public void execute(String strPacket, NetworkClient client, PacketByteBuf buf) {
        if (!this.packets.containsKey(strPacket)) {
            AtlasNetwork.logger.warn("{} requested an unknown packet '{}'", client.remoteAddress(), strPacket);

            PacketByteBuf response = PacketByteBuf.create()
                    .writeString("request_fail")
                    .writeString(NetworkErrors.UNKNOWN_PACKET);

            client.sendPacket(response);
            buf.release();
            return;
        }

        NetworkPacket packet = this.packets.get(strPacket);
        this.executor.submit(() -> packet.onReceive(client, buf));
    }

    @Override
    public void register(NetworkPacket packet) {
        this.packets.put(packet.getKey(), packet);
    }
}
