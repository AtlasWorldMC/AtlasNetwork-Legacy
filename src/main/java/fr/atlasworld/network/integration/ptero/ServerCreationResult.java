package fr.atlasworld.network.integration.ptero;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import org.jetbrains.annotations.Nullable;

public record ServerCreationResult(boolean success, String msg, @Nullable ApplicationServer server) {
}
