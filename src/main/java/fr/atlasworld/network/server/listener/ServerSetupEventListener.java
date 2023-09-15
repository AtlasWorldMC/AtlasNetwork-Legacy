package fr.atlasworld.network.server.listener;

import com.mattmalec.pterodactyl4j.client.entities.Directory;
import com.mattmalec.pterodactyl4j.client.managers.UploadFileAction;
import com.mattmalec.pterodactyl4j.client.ws.events.install.InstallCompletedEvent;
import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.server.configuration.ServerFileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Uploads and handles servers automatically without needing human interaction
 */
public class ServerSetupEventListener extends ClientSocketListenerAdapter {
    private final @Nullable ServerFileConfiguration configuration;

    public ServerSetupEventListener(@Nullable ServerFileConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onInstallCompleted(InstallCompletedEvent event) {
        if (this.configuration == null || this.configuration.files().isEmpty()) {
            AtlasNetwork.logger.info("{} has finished installation, starting server..", event.getServer().getName());
            event.getServer().start().executeAsync();
            return;
        }

        AtlasNetwork.logger.info("{} has finished installation, uploading files..", event.getServer().getName());
        File zipFile = new File(this.configuration.id() + ".zip");

        try (FileOutputStream outStream = new FileOutputStream(zipFile);
             ZipOutputStream zipStream = new ZipOutputStream(outStream)) {

            for (ServerFileConfiguration.ServerFile file : this.configuration.files()) {
                FileManager.archiveFile(file.getLocationAsFile(), file.remoteLocation(), zipStream);
            }
        } catch (IOException e) {
            AtlasNetwork.logger.error("Could not upload server files for {}, reinstalling server..", event.getServer().getName(), e);
            event.getServer().getManager().reinstall().executeAsync();
            return;
        }

        Directory serverRootDir = event.getServer().retrieveDirectory().execute();

        UploadFileAction uploadAction = serverRootDir.upload();
        uploadAction.addFile(zipFile);
        uploadAction.execute();

        //Update the server directory
        serverRootDir = event.getServer().retrieveDirectory().execute();
        com.mattmalec.pterodactyl4j.client.entities.File remoteFile = serverRootDir.getFileByName(this.configuration.id() + ".zip").get();
        remoteFile.decompress().execute();
        remoteFile.delete().execute();

        AtlasNetwork.logger.info("Finished uploading files for {}, starting server..", event.getServer().getName());
        event.getServer().start().executeAsync();
    }
}
