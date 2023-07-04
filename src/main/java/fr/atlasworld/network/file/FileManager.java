package fr.atlasworld.network.file;

import fr.atlasworld.network.AtlasNetwork;

import java.io.File;

public class FileManager {
    //Static Fields
    public static final String SETTINGS = "settings.json";

    public static File getWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    public static File getWorkingDirectoryFile(String filename) {
        return new File(getWorkingDirectory(), filename);
    }

    public static File getSettingsFile() {
        return getWorkingDirectoryFile(SETTINGS);
    }
}
