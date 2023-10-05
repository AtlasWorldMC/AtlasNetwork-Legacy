package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.exception.networking.packet.PacketException;
import fr.atlasworld.network.api.exception.networking.packet.UnknownPacketException;
import fr.atlasworld.network.api.exception.networking.unchecked.UncheckedNetworkException;
import fr.atlasworld.network.api.networking.packet.NetworkPacket;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.entities.NetworkClient;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
        this(Executors.newFixedThreadPool(32), new HashMap<>());
    }

    @Override
    public void execute(String packetKey, NetworkClient client, PacketByteBuf buf) throws PacketException {
        if (!this.packets.containsKey(packetKey)) {
            throw new UnknownPacketException(packetKey, client);
        }

        NetworkPacket packet = this.packets.get(packetKey);
        this.executor.submit(() -> {
            try {
                packet.onReceived(buf, client);
            } catch (Exception e) {
                throw new UncheckedNetworkException(e);
            }
        });
    }

    @Override
    public void registerPacket(fr.atlasworld.network.api.networking.packet.NetworkPacket packet) {

    }

    @Override
    public void unregisterPacket(String id) {

    }

    @Override
    public void unregisterPacket(fr.atlasworld.network.api.networking.packet.NetworkPacket packet) {

    }

    @Override
    public Set<fr.atlasworld.network.api.networking.packet.NetworkPacket> getPackets() {
        return null;
    }
}
