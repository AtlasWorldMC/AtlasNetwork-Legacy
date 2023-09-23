package fr.atlasworld.network.boot;

import fr.atlasworld.network.AtlasNetwork;

public class AtlasNetworkBootstrap {
    public static void main(String[] args) {
        AtlasNetwork server = new AtlasNetwork();

        fr.atlasworld.network.api.AtlasNetwork.setServer(server);


    }
}
