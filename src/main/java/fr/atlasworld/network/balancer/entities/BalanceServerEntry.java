package fr.atlasworld.network.balancer.entities;

import java.net.InetSocketAddress;

public interface BalanceServerEntry {
    String name();
    InetSocketAddress address();
}
