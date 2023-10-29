package fr.atlasworld.network.services.panel.entities;

import fr.atlasworld.network.NetworkEntity;
import fr.atlasworld.network.services.panel.schema.ServerSchema;

public interface PanelServer extends NetworkEntity {
    /**
     * Retrieve the name of the server
     */
    String name();

    /**
     * Retrieve the source instruction schema of the server
     */
    ServerSchema sourceSchema();
}
