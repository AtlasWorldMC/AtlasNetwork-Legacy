package fr.atlasworld.network.networking.packet;

import java.util.ArrayList;
import java.util.List;

public class PacketManager {
    private final List<NetworkPacket> registeredPackets;

    private PacketManager(List<NetworkPacket> registeredPackets) {
        this.registeredPackets = registeredPackets;
    }

    public void registerPacket(NetworkPacket packet) {
        this.registeredPackets.add(packet);
    }

    public NetworkPacket getPacket(String packetId) {
        return this.registeredPackets.stream()
                .filter(packet -> packet.getIdentifier().equals(packetId))
                .findFirst().orElse(null);
    }

    public List<NetworkPacket> getRegisteredPackets() {
        return registeredPackets;
    }

    //Static fields
    private static PacketManager manager;

    public static PacketManager getManager() {
        if (manager == null) {
            manager = new PacketManager(new ArrayList<>());
        }

        return manager;
    }
}
