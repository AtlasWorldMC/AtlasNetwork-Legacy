package fr.atlasworld.network.balancer;

import fr.atlasworld.network.balancer.entities.BalanceServerEntry;
import fr.atlasworld.network.exceptions.requests.RequestException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Load Balancer, configures the load balancer at runtime for in real time load balancing
 */
public interface LoadBalancer {
    /**
     * Gets all the servers registered on the load balancer
     * @return registered servers
     */
    List<BalanceServerEntry> getServers();

    /**
     * Adds/registers a server to the load balancer
     * @param name server name
     * @param address server address
     * @throws RequestException thrown if something went wrong
     */
    void addServer(String name, InetSocketAddress address) throws RequestException;

    /**
     * Removes/unregisters a server from the load balance
     * @param entry server
     * @throws RequestException thrown if something went wrong
     */
    void removeServer(BalanceServerEntry entry) throws RequestException;

    /**
     * Removes/unregisters a server from the load balance
     * @param entry server
     * @throws RequestException thrown if something went wrong
     */
    void removeServer(String entry) throws RequestException;
}
