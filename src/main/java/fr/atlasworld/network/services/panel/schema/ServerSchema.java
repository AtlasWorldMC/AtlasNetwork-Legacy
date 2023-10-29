package fr.atlasworld.network.services.panel.schema;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This contains every instruction to create a server.
 */
public record ServerSchema(String id, String description, ServerSchemaResources resources, ServerSchemaEgg egg,
                           ServerSchemaMisc misc, JsonObject variables, List<ServerSchemaFile> files) {

    /**
     * Resource allocation for the server
     * @param allocations the number of network allocations the server requires (Network Allocation = Port/Address)
     * @param cpu the max cpu percentage allocated to the server
     * @param memory the max memory allocated to the server
     * @param swap the max swap allocated to the server
     * @param disk the disk space allocated to the server
     */
    public record ServerSchemaResources(int allocations, int cpu, int memory, Integer swap, int disk) {}

    /**
     * Specifies Ptero's Egg file
     */
    public record ServerSchemaEgg(int nest, int egg) {}

    /**
     * Misc & optional settings for the server
     * @param image Docker Image for the server
     * @param launch Launch command for the server
     */
    public record ServerSchemaMisc(@Nullable String image, @Nullable String launch) {}

    /**
     * File operation for the server
     * @param location local location, or url to download from.
     * @param remoteLocation file location on the remote server
     */
    public record ServerSchemaFile(String location, @SerializedName("remote_location") String remoteLocation) {}

    public static final ServerSchema EXAMPLE = new ServerSchema(
            "example",
            "Example server Schema",
            new ServerSchemaResources(
                    1,
                    100,
                    4096,
                    2048,
                    10240
            ),
            new ServerSchemaEgg(
                    0,
                    0
            ),
            new ServerSchemaMisc(
                    "",
                    ""
            ),
            new JsonObject(),
            List.of(new ServerSchemaFile("example/example.file", "example.file"))
    );
}
