package fr.atlasworld.network.file;

import java.io.File;

public class FileManager {
    //Static Fields
    public static final String CONFIG = "config.json";

    public static File getWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    public static File getWorkingDirectoryFile(String filename) {
        return new File(getWorkingDirectory(), filename);
    }

    public static File getConfigFile() {
        return getWorkingDirectoryFile(CONFIG);
    }

    public static File getServerConfigDirectory() {
        return getWorkingDirectoryFile("servers");
    }
}
