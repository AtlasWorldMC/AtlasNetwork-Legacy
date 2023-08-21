package fr.atlasworld.network.integration.ptero;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;

public record PteroServer(ApplicationServer remoteServer, String type) {
}
