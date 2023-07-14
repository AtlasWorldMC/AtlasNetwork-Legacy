package fr.atlasworld.network.networking.entities;

import io.netty.channel.Channel;

public class Client {
    private final Channel channel;

    public Client(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
