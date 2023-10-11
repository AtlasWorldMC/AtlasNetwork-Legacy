package fr.atlasworld.network.api.file.loader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Loads the file as a string
 * <p>
 * File from Crafted Launcher find the original file <a href="https://github.com/Raft08/Crafted-Launcher/blob/main/src/main/java/be/raft/launcher/file/loader/StringFileLoader.java">here</a>
 * @author RaftDev
 */
public class StringFileLoader extends FileLoader<String> {
    public StringFileLoader(File file) {
        super(file);
    }

    @Override
    public String load() {
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load file '" + this.file + "':", e);
        }
    }

    @Override
    public void save(String value) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write(value);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save file '" + this.file + "':", e);
        }
    }
}
