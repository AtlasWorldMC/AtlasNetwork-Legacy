package fr.atlasworld.network.server.listener;

import com.mattmalec.pterodactyl4j.client.ws.events.install.InstallCompletedEvent;
import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.server.configuration.ServerFileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.zip.ZipOutputStream;

public class ServerSetupEventListener extends ClientSocketListenerAdapter {
    private final @Nullable ServerFileConfiguration configuration;

    public ServerSetupEventListener(@Nullable ServerFileConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onInstallCompleted(InstallCompletedEvent event) {
        if (this.configuration == null) {
            AtlasNetwork.logger.info("{} has finished installation, starting server..", event.getServer().getName());
            event.getServer().start().executeAsync();
            return;
        }

        AtlasNetwork.logger.info("{} has finished installation, uploading files..", event.getServer().getName());

        try (FileOutputStream byteStream = new FileOutputStream("test.zip");
             ZipOutputStream zipStream = new ZipOutputStream(byteStream)) {

            for (ServerFileConfiguration.ServerFile file : this.configuration.files()) {
                FileManager.archiveFile(file.getLocationAsFile(), file.remoteLocation(), zipStream);
            }

        } catch (IOException e) {
            AtlasNetwork.logger.error("Could not upload server files for {}, reinstalling server..", event.getServer().getName(), e);
            event.getServer().getManager().reinstall().executeAsync();
        }
    }
}
