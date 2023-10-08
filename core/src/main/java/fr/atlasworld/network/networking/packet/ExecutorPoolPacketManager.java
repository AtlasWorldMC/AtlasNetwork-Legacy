package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.exception.networking.packet.PacketException;
import fr.atlasworld.network.api.exception.networking.packet.PacketExecutionException;
import fr.atlasworld.network.api.exception.networking.packet.UnknownPacketException;
import fr.atlasworld.network.api.networking.packet.NetworkPacket;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.entities.NetworkClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes the packets with an executor instead of blocking for every packet received,
 * that makes this system able to execute multiple packets on the same time
 * @see PacketManager
 */
public class ExecutorPoolPacketManager implements PacketManager {
    private final ExecutorService executor;
    private final Map<String, NetworkPacket> packets;

    public ExecutorPoolPacketManager(ExecutorService executor, Map<String, NetworkPacket> packets) {
        this.executor = executor;
        this.packets = packets;
    }

    public ExecutorPoolPacketManager() {
        this(Executors.newFixedThreadPool(32), new HashMap<>());
    }

    @Override
    public void execute(String packetKey, NetworkClient client, PacketByteBuf buf) throws PacketException {
        if (!this.packets.containsKey(packetKey)) {
            throw new UnknownPacketException(packetKey, client);
        }

        NetworkPacket packet = this.packets.get(packetKey);
        try {
            this.executor.submit(() -> {
                try {
                    packet.onReceived(buf, client);
                } catch (Exception e) {
                    throw new RuntimeException(new PacketExecutionException(packetKey, e));
                }
            });
        } catch (RuntimeException e) {
            throw (PacketException) e.getCause();
        }
    }

    @Override
    public void registerPacket(NetworkPacket packet) {
        this.packets.put(packet.getId(), packet);
    }

    @Override
    public void unregisterPacket(String id) {
        this.packets.remove(id);
    }

    @Override
    public void unregisterPacket(NetworkPacket packet) {
        this.unregisterPacket(packet.getId());
    }

    @Override
    public Set<NetworkPacket> getPackets() {
        return new HashSet<>(this.packets.values());
    }
}
