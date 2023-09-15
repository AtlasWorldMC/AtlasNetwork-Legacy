package fr.atlasworld.network.file.loader;

import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Loads the file as a json and process it using Gson
 * <p>
 * File from Crafted Launcher find the original file <a href="https://github.com/Raft08/Crafted-Launcher/blob/main/src/main/java/be/raft/launcher/file/loader/GsonFileLoader.java">here</a>
 * @param <T> the target java object
 * @author RaftDev
 */
public class GsonFileLoader<T> extends FileLoader<T> {
    private final Gson gson;
    private final Type type;

    public GsonFileLoader(File file, Type type) {
        super(file);
        this.gson = new Gson();
        this.type = type;
    }

    public GsonFileLoader(File file, Gson gson, Type type) {
        super(file);
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T load() {
        return this.gson.fromJson(new StringFileLoader(this.file).load(), this.type);
    }

    @Override
    public void save(T value) {
        new StringFileLoader(this.file).save(this.gson.toJson(value));
    }
}
