package fr.atlasworld.network.balancer.haproxy;

import fr.atlasworld.network.balancer.entities.BalanceServerEntry;

import java.net.InetSocketAddress;

/**
 * HAProxy Balance Server entry implementation
 * @param name name of the server
 * @param address address of the server
 */
public record HAProxyServer(String name, InetSocketAddress address) implements BalanceServerEntry {
    public String toJson() {
        return "{\"check\":\"enabled\",\"send-proxy\":\"enabled\",\"address\":\"" + this.address.getHostName() +
                "\",\"name\":\"" + this.name + "\",\"port\":" + address.getPort() + "}";
    }
}
