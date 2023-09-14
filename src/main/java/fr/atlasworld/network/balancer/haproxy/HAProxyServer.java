package fr.atlasworld.network.balancer.haproxy;

import fr.atlasworld.network.balancer.entities.BalanceServerEntry;

import java.net.InetSocketAddress;

public record HAProxyServer(String name, InetSocketAddress address) implements BalanceServerEntry {
    public String toJson() {
        return "{\"check\":\"enabled\",\"send-proxy\":\"enabled\",\"address\":\"" + this.address.getHostName() +
                "\",\"name\":\"" + this.name + "\",\"port\":" + address.getPort() + "}";
    }
}
