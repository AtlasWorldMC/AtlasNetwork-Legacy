package fr.atlasworld.network.server.entities;

import java.io.File;

public record UploadAction(File localFile, String remoteName) {
}
