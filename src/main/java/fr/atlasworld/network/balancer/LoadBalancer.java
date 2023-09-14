package fr.atlasworld.network.balancer;

import fr.atlasworld.network.balancer.entities.BalanceServerEntry;
import fr.atlasworld.network.exceptions.requests.RequestException;

import java.net.InetSocketAddress;
import java.util.List;

public interface LoadBalancer {
    List<BalanceServerEntry> getServers();
    void addServer(String name, InetSocketAddress address) throws RequestException;
    void removeServer(BalanceServerEntry entry) throws RequestException;
    void removeServer(String entry) throws RequestException;
}
