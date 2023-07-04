package fr.atlasworld.network.utils;

import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;

public class Settings {
    private final boolean primaryNode;
    private final String nodeName;
    private final String socketHost;
    private final int socketPort;

    private Settings(boolean primaryNode, String nodeName, String socketHost, int socketPort) {
        this.primaryNode = primaryNode;
        this.nodeName = nodeName;
        this.socketHost = socketHost;
        this.socketPort = socketPort;
    }

    public boolean isPrimaryNode() {
        return primaryNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getSocketHost() {
        return socketHost;
    }

    public int getSocketPort() {
        return socketPort;
    }

    //Static fields
    private static Settings settings;

    public static Settings getSettings() {
        if (settings == null) {
            settings = new GsonFileLoader<Settings>(FileManager.getSettingsFile(), Settings.class).load();
        }
        return settings;
    }
}
