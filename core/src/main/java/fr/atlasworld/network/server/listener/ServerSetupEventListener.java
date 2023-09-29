package fr.atlasworld.network.server.listener;

import com.google.gson.JsonObject;
import com.mattmalec.pterodactyl4j.client.entities.Directory;
import com.mattmalec.pterodactyl4j.client.managers.UploadFileAction;
import com.mattmalec.pterodactyl4j.client.ws.events.install.InstallCompletedEvent;
import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;
import fr.atlasworld.network.AtlasNetworkOld;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.server.configuration.ServerFileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

/**
 * Uploads and handles servers automatically without needing human interaction
 */
public class ServerSetupEventListener extends ClientSocketListenerAdapter {
    private final ServerFileConfiguration configuration;
    private final Database<AuthenticationProfile> database;
    private final boolean notifyProxies;
    private final @Nullable UUID requestId;

    public ServerSetupEventListener(ServerFileConfiguration configuration, Database<AuthenticationProfile> database, boolean notifyProxies, @Nullable UUID requestId) {
        this.configuration = configuration;
        this.database = database;
        this.notifyProxies = notifyProxies;
        this.requestId = requestId;
    }

    @Override
    public void onInstallCompleted(InstallCompletedEvent event) {
        //Auth configuration file
        AtlasNetworkOld.logger.info("Creating authentication token file for {}..", event.getServer().getName());
        String authToken = AtlasNetworkOld.getSecurityManager().generateAuthenticationToken();
        String hashedToken = AtlasNetworkOld.getSecurityManager().hash(authToken);

        AuthenticationProfile profile = new AuthenticationProfile(event.getServer().getUUID().toString(), hashedToken);

        JsonObject jsonProfile = new JsonObject();
        jsonProfile.addProperty("uuid", event.getServer().getUUID().toString());
        jsonProfile.addProperty("token", authToken);

        try {
            this.database.save(profile);
        } catch (DatabaseException e) {
            AtlasNetworkOld.logger.error("Could not generate token file for {}, reinstalling server..", event.getServer().getName(), e);
            event.getServer().getManager().reinstall().executeAsync();
            return;
        }

        Directory serverRootDir = event.getServer().retrieveDirectory().execute();
        serverRootDir.createFile("network-credentials.json", jsonProfile.toString()).execute();

        if (this.configuration.files().isEmpty()) {
            AtlasNetworkOld.logger.info("{} has finished installation, starting server..", event.getServer().getName());
            event.getServer().start().executeAsync();
            return;
        }

        AtlasNetworkOld.logger.info("{} has finished installation, uploading files..", event.getServer().getName());
        File zipFile = new File(this.configuration.id() + ".zip");

        try (FileOutputStream outStream = new FileOutputStream(zipFile);
             ZipOutputStream zipStream = new ZipOutputStream(outStream)) {

            for (ServerFileConfiguration.ServerFile file : this.configuration.files()) {
                FileManager.archiveFile(file.getLocationAsFile(), file.remoteLocation(), zipStream);
            }
        } catch (IOException e) {
            AtlasNetworkOld.logger.error("Could not upload server files for {}, reinstalling server..", event.getServer().getName(), e);
            event.getServer().getManager().reinstall().executeAsync();
            return;
        }

        UploadFileAction uploadAction = serverRootDir.upload();
        uploadAction.addFile(zipFile);
        uploadAction.execute();

        //Update the server directory
        serverRootDir = event.getServer().retrieveDirectory().execute();
        com.mattmalec.pterodactyl4j.client.entities.File remoteFile = serverRootDir.getFileByName(this.configuration.id() + ".zip").get();
        remoteFile.decompress().execute();
        remoteFile.delete().execute();

        AtlasNetworkOld.logger.info("Finished uploading files for {}, starting server..", event.getServer().getName());
        event.getServer().start().executeAsync();
    }
}
