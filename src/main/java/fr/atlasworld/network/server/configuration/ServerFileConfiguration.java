package fr.atlasworld.network.server.configuration;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public record ServerFileConfiguration(String id, String version, List<ServerFile> files) {
    public record ServerFile(String name, String type, String location,
                              @SerializedName("remote_location") String remoteLocation,
                              @Nullable @SerializedName("process_file") String processFile) {

        public File getLocationAsFile() {
            return new File(this.location);
        }

        public File getRemoteAsFile() {
            return new File(this.remoteLocation);
        }
    }
}
