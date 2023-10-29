package fr.atlasworld.network.services.panel;

import fr.atlasworld.network.services.Service;
import fr.atlasworld.network.services.panel.entities.PanelServer;
import fr.atlasworld.network.services.panel.schema.ServerSchema;
import fr.atlasworld.network.services.panel.exceptions.PanelException;

public interface PanelService extends Service {

    /**
     * Create a server with the specified schema.
     * WARNING: THIS METHOD IS REALLY EXPENSIVE FOR ATLAS NETWORK AND FOR THE SYSTEMS RUNNING THE SERVERS!
     * @return newly created server.
     */
    PanelServer createServer(String name, ServerSchema schema) throws PanelException;

    /**
     * Create a server with the specified schema id.
     * WARNING: THIS METHOD IS REALLY EXPENSIVE FOR ATLAS NETWORK AND FOR THE SYSTEMS RUNNING THE SERVERS!
     * @return newly created server.
     * @throws fr.atlasworld.network.services.panel.exceptions.unchecked.UnknownServerSchemaPanelException if no schema could be found with the specified ID.
     */
    PanelServer createServer(String name, String schemaId) throws PanelException;
}
