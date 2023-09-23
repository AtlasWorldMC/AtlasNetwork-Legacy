package fr.atlasworld.network.server.configuration;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Upload Index Server Object
 * @param id server id
 * @param version server version -> Should always match the ServerConfiguration version
 * @param files files to upload to that server
 */
public record ServerFileConfiguration(String id, String version, List<ServerFile> files) {

    /**
     * Server File object, defines server file
     * @param name file name
     * @param type file type
     * @param location file local location
     * @param remoteLocation file remote location
     */
    public record ServerFile(String name, String type, String location,
                              @SerializedName("remote_location") String remoteLocation) {

        public File getLocationAsFile() {
            return new File(this.location);
        }

        public File getRemoteAsFile() {
            return new File(this.remoteLocation);
        }
    }
}
