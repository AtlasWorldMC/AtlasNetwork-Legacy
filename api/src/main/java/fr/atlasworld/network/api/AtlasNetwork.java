package fr.atlasworld.network.api;

public final class AtlasNetwork {
    private static AtlasNetworkServer server;

    public static AtlasNetworkServer getServer() {
        return server;
    }

    public static void setServer(AtlasNetworkServer server) {
        if (AtlasNetwork.server != null) {
            throw new UnsupportedOperationException("Cannot redefine server!");
        }

        AtlasNetwork.server = server;
    }
}
