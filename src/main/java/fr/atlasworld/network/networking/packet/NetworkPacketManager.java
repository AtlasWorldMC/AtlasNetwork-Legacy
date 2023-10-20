package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.entities.NetworkClient;
import fr.atlasworld.network.networking.packet.exceptions.PacketExecutionException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Network's default Packet manager, Executes the packets with an executor instead of blocking for every packet received,
 * that makes this system able to execute multiple packets on the same time
 * @see PacketManager
 */
public class NetworkPacketManager implements PacketManager {
    private final ExecutorService executor;
    private final Map<String, NetworkPacket> packets;

    public NetworkPacketManager(ExecutorService executor, Map<String, NetworkPacket> packets) {
        this.executor = executor;
        this.packets = packets;
    }

    public NetworkPacketManager() {
        this(Executors.newFixedThreadPool(8), new HashMap<>());
    }

    @Override
    public void execute(String strPacket, NetworkClient client, PacketByteBuf buf) {
        if (!this.packets.containsKey(strPacket)) {
            AtlasNetwork.logger.warn("{} requested an unknown packet '{}'", client.remoteAddress(), strPacket);

            PacketByteBuf response = new PacketByteBuf(client.getChannel().alloc().buffer())
                    .writeString("request_fail")
                    .writeString(NetworkErrors.UNKNOWN_PACKET);

            client.sendPacket(response);
            buf.release();
            return;
        }

        NetworkPacket packet = this.packets.get(strPacket);
        this.executor.submit(() -> {
            try {
                packet.onReceive(client, buf);
            } catch (Throwable e) {
                PacketExecutionException ex = new PacketExecutionException(e);
                AtlasNetwork.logger.error("Failed to execute '{}'", strPacket, ex);
            }
        });
    }

    @Override
    public void register(NetworkPacket packet) {
        this.packets.put(packet.getKey(), packet);
    }
}
