package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.networking.NetworkSource;
import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.api.networking.packet.SentPacket;

public class SentPacketImpl implements SentPacket {
    private final PacketByteBuf buffer;
    private final NetworkSource source;

    public SentPacketImpl(PacketByteBuf buffer, NetworkSource source) {
        this.buffer = buffer;
        this.source = source;
    }

    @Override
    public void cleanUpResources() {
        while (this.buffer.isAccessible()) {
            this.buffer.release();
        }
    }

    @Override
    public PacketByteBuf getBufferData() {
        return this.buffer;
    }

    @Override
    public NetworkSource getTarget() {
        return this.source;
    }
}
