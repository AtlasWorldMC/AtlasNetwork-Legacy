package fr.atlasworld.network.file.loader;

import java.io.File;
import java.io.IOException;

/**
 * File Loader, loads the files in memory
 * <p>
 * File from Crafted Launcher find the original file <a href="https://github.com/Raft08/Crafted-Launcher/blob/main/src/main/java/be/raft/launcher/file/loader/FileLoader.java">here</a>
 * @param <T> output data type
 * @author RaftDev
 */
public abstract class FileLoader<T> {
    protected final File file;

    public FileLoader(File file) {
        this.file = file;
    }

    /**
     * Loads the file in memory
     * @return loaded & processed file
     */
    public abstract T load() throws IOException;

    /**
     * Saves the file to the disk
     * @param value data to save
     */
    public abstract void save(T value) throws IOException;

    /**
     * Checks if the file exists
     * @return true if the file exist
     */
    public boolean fileExists() {
        return file.isFile();
    }

    /**
     * Creates the file on the file-system
     */
    public void createFile() throws IOException {
        if (!this.file.getParentFile().isDirectory()) {
            this.file.getParentFile().mkdirs();
        }

        this.file.createNewFile();
    }

    /**
     * Gets the file
     * @return file
     */
    public File getFile() {
        return file;
    }
}
