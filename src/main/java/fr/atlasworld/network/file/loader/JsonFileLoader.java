package fr.atlasworld.network.file.loader;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;

/**
 * Loads the file as a json
 * <p>
 * File from Crafted Launcher find the original file <a href="https://github.com/Raft08/Crafted-Launcher/blob/main/src/main/java/be/raft/launcher/file/loader/JsonFileLoader.java">here</a>
 * @author RaftDev
 */
public class JsonFileLoader extends FileLoader<JsonElement> {
    private final boolean prettyPrint;

    public JsonFileLoader(File file) {
        super(file);
        this.prettyPrint = false;
    }

    public JsonFileLoader(File file, boolean prettyPrint) {
        super(file);
        this.prettyPrint = prettyPrint;
    }

    @Override
    public JsonElement load() throws IOException {
        return JsonParser.parseString(new StringFileLoader(file).load());
    }

    @Override
    public void save(JsonElement value) throws IOException {
        StringFileLoader loader = new StringFileLoader(file);
        GsonBuilder builder = new GsonBuilder();
        if (prettyPrint) {
            loader.save(builder.setPrettyPrinting().create().toJson(value));
        } else {
            loader.save(builder.create().toJson(value));
        }
    }

    @Override
    public void createFile() throws IOException {
        super.createFile();
        this.save(new JsonObject());
    }
}
