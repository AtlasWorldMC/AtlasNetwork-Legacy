package fr.atlasworld.network.old_networking.entities;

import fr.atlasworld.network.networking.packet.PacketByteBuf;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class Client {
    private final Channel channel;

    public Client(Channel channel) {
        this.channel = channel;
    }

    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    public void sendPacket(PacketByteBuf buf) {
        this.channel.writeAndFlush(buf.getParent());
    }

    public Channel getChannel() {
        return channel;
    }
}
