package fr.atlasworld.network.services.panel.pteroq.entities;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import fr.atlasworld.network.services.panel.entities.PanelServer;
import fr.atlasworld.network.services.panel.schema.ServerSchema;

import java.util.UUID;

public class PteroqServer implements PanelServer {
    private final ApplicationServer server;
    private final ServerSchema schema;

    public PteroqServer(ApplicationServer server, ServerSchema schema) {
        this.server = server;
        this.schema = schema;
    }

    @Override
    public UUID id() {
        return this.server.getUUID();
    }

    @Override
    public String name() {
        return this.server.getName();
    }

    @Override
    public ServerSchema sourceSchema() {
        return this.schema;
    }
}
