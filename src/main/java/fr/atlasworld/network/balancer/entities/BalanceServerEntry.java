package fr.atlasworld.network.balancer.entities;

import java.net.InetSocketAddress;

/**
 * Instance of a server on the load balancer
 */
public interface BalanceServerEntry {
    String name();
    InetSocketAddress address();
}
