package fr.atlasworld.network.api.event.networking.packet;

import fr.atlasworld.network.api.event.components.CancellableEvent;
import fr.atlasworld.network.api.event.components.Event;
import fr.atlasworld.network.api.event.networking.NetworkEvent;
import fr.atlasworld.network.api.networking.NetworkSocket;
import fr.atlasworld.network.api.networking.NetworkSource;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;

public class PacketNetworkEvent extends NetworkEvent implements CancellableEvent {
    private final NetworkSource source;
    private final PacketByteBuf dataBuffer;
    private boolean cancelled = false;

    public PacketNetworkEvent(NetworkSocket socket, NetworkSource source, PacketByteBuf dataBuffer) {
        super(socket);
        this.source = source;
        this.dataBuffer = dataBuffer;
    }

    public NetworkSource getSource() {
        return source;
    }

    public PacketByteBuf getDataBuffer() {
        return dataBuffer;
    }

    @Override
    public boolean cancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
