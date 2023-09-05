package fr.atlasworld.network.server.pterodactyl;

import com.mattmalec.pterodactyl4j.UtilizationState;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.Directory;
import com.mattmalec.pterodactyl4j.client.managers.UploadFileAction;
import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;
import fr.atlasworld.network.database.entities.server.DatabaseServer;
import fr.atlasworld.network.server.configuration.ServerConfiguration;
import fr.atlasworld.network.server.entities.PanelServer;
import fr.atlasworld.network.server.entities.ServerStatus;
import fr.atlasworld.network.server.entities.UploadAction;

public class PteroServer implements PanelServer {
    private final ClientServer clientServer;
    private final ApplicationServer appServer;
    private final ServerConfiguration configuration;
    private final DatabaseServer databaseServer;

    public PteroServer(ClientServer clientServer, ApplicationServer appServer, ServerConfiguration configuration, DatabaseServer databaseServer) {
        this.clientServer = clientServer;
        this.appServer = appServer;
        this.configuration = configuration;
        this.databaseServer = databaseServer;
    }

    @Override
    public String name() {
        return this.appServer.getName();
    }

    @Override
    public void name(String name) {
        this.appServer.getDetailManager().setName(name).executeAsync();
    }

    @Override
    public String description() {
        return this.appServer.getDescription();
    }

    @Override
    public void description(String description) {
        this.appServer.getDetailManager().setDescription(description);
    }

    @Override
    public ServerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public DatabaseServer getDatabaseServer() {
        return this.databaseServer;
    }

    @Override
    public ServerStatus status() {
        return switch (this.appServer.getStatus()) {
            case SUSPENDED -> ServerStatus.SUSPENDED;
            case INSTALLING, RESTORING_BACKUP -> ServerStatus.INSTALLING;
            case INSTALL_FAILED -> ServerStatus.CORRUPTED;
            case UNKNOWN -> {
                UtilizationState state = this.clientServer.retrieveUtilization().execute().getState();
                yield switch (state) {
                    case OFFLINE -> ServerStatus.OFFLINE;
                    case STARTING -> ServerStatus.STARTING;
                    case RUNNING -> ServerStatus.ONLINE;
                    case STOPPING -> ServerStatus.STOPPING;
                };
            }
        };
    }

    @Override
    public void reinstall() {
        this.appServer.getController().reinstall();
    }

    @Override
    public void delete() {
        this.appServer.getController().delete(false).executeAsync();
    }

    @Override
    public void delete(boolean force) {
        this.appServer.getController().delete(force).executeAsync();
    }

    @Override
    public void start() {
        this.clientServer.start();
    }

    @Override
    public void restart() {
        this.clientServer.restart();
    }

    @Override
    public void stop() {
        this.clientServer.stop();
    }

    @Override
    public void kill() {
        this.clientServer.kill();
    }

    @Override
    public void upload(String directory, UploadAction... uploads) {
        Directory uploadDir = this.clientServer.retrieveDirectory(directory).execute();
        UploadFileAction action = this.clientServer.getFileManager().upload(uploadDir);

        for (UploadAction uploadFile : uploads) {
            action.addFile(uploadFile.localFile(), uploadFile.remoteName());
        }

        action.executeAsync();
    }

    @Override
    public void addListener(ClientSocketListenerAdapter listener) {
        this.clientServer.getWebSocketBuilder().addEventListeners(listener).build();
    }

    @Override
    public void sendCommand(String command) {
        this.clientServer.sendCommand(command);
    }
}
