package fr.atlasworld.network.server.listener;

import com.google.gson.JsonObject;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.Directory;
import com.mattmalec.pterodactyl4j.client.managers.UploadFileAction;
import com.mattmalec.pterodactyl4j.client.ws.events.StatusUpdateEvent;
import com.mattmalec.pterodactyl4j.client.ws.events.install.InstallCompletedEvent;
import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.session.SessionManager;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.configuration.ServerFileConfiguration;
import fr.atlasworld.network.server.entities.ServerStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

/**
 * Uploads and handles servers automatically without needing human interaction
 */
public class ServerSetupEventListener extends ClientSocketListenerAdapter {
    private final @Nullable ServerFileConfiguration configuration;
    private final Database<AuthenticationProfile> database;
    private final boolean notifyProxies;

    public ServerSetupEventListener(@Nullable ServerFileConfiguration configuration, Database<AuthenticationProfile> database, boolean notifyProxies) {
        this.configuration = configuration;
        this.database = database;
        this.notifyProxies = notifyProxies;
    }

    @Override
    public void onInstallCompleted(InstallCompletedEvent event) {
        //Auth configuration file
        AtlasNetwork.logger.info("Creating authentication token file for {}..", event.getServer().getName());
        String authToken = AtlasNetwork.getSecurityManager().generateAuthenticationToken();
        String hashedToken = AtlasNetwork.getSecurityManager().hash(authToken);

        AuthenticationProfile profile = new AuthenticationProfile(event.getServer().getUUID().toString(), hashedToken);

        JsonObject jsonProfile = new JsonObject();
        jsonProfile.addProperty("uuid", event.getServer().getUUID().toString());
        jsonProfile.addProperty("token", authToken);

        try {
            this.database.save(profile);
        } catch (DatabaseException e) {
            AtlasNetwork.logger.error("Could not generate token file for {}, reinstalling server..", event.getServer().getName(), e);
            event.getServer().getManager().reinstall().executeAsync();
            return;
        }

        Directory serverRootDir = event.getServer().retrieveDirectory().execute();
        serverRootDir.createFile("network-credentials.json", jsonProfile.toString()).execute();

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

    @Override
    public void onStatusUpdate(StatusUpdateEvent event) {
        switch (event.getState()) {
            case RUNNING -> this.onServerStarted(event.getServer());
        }
    }

    private void onServerStarted(ClientServer server) {
        if (this.notifyProxies) {
            ServerManager serverManager = AtlasNetwork.getServerManager();
            SessionManager sessionManager = AtlasNetwork.getSessionManager();

            serverManager.getServers().stream()
                    .filter(pServer -> pServer.getConfiguration().equals(serverManager.defaultProxyConfiguration()))
                    .filter(proxy -> proxy.status() == ServerStatus.ONLINE)
                    .map(proxy ->  sessionManager.getSession(proxy.id()))
                    .filter(Objects::nonNull)
                    .forEach(client -> {
                        PacketByteBuf addServerPacket = PacketByteBuf.create()
                                .writeString("update_servers")
                                .writeByte((byte) 0x01)
                                .writeInt(1)
                                .writeString(server.getName())
                                .writeString(server.getPrimaryAllocation().getIP())
                                .writeInt(server.getPrimaryAllocation().getPortInt());

                        client.sendPacket(addServerPacket);
                    });
        }
    }
}
