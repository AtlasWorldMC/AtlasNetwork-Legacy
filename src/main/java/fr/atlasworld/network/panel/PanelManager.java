package fr.atlasworld.network.panel;

import com.google.gson.JsonElement;
import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.*;
import com.mattmalec.pterodactyl4j.application.managers.ServerCreationAction;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.EggConfig;
import fr.atlasworld.network.config.PanelConfig;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.exceptions.panel.PanelNotEnoughAllocationException;
import fr.atlasworld.network.exceptions.panel.PanelServerNotExistException;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PanelManager {
    private final PteroApplication api;
    private final PteroClient client;
    private final PanelConfig config;
    private final Database<ServerInfo> database;
    private final Map<ApplicationServer, ClientServer> servers;

    public PanelManager(PanelConfig config, Database<ServerInfo> database) {
        this.api = PteroBuilder.createApplication(config.url(), config.token());
        this.client = PteroBuilder.createClient(config.url(), config.token());
        this.config = config;
        this.database = database;
        this.servers = new HashMap<>();
    }

    public void init() throws PanelException {
        List<ApplicationServer> availableServers = this.api.retrieveServers().execute()
                .stream()
                .filter(server -> server.getOwnerIdLong() == this.config.userId())
                .toList();

        for (ApplicationServer server : availableServers) {
            try {
                if (!this.database.has(server.getUUID().toString())) {
                    AtlasNetwork.logger.info("Found unknown server '{}', deleting server from account..", server.getName());
                    server.getController().delete(false).execute();
                }
                this.servers.putIfAbsent(server, this.client.retrieveServerByIdentifier(server.getIdentifier()).execute());
            } catch (DatabaseException e) {
                throw new PanelException("Could not verify server", e);
            }
        }

        try {
            ServerInfo proxyServer = this.database.getAllEntries().stream()
                    .filter(server -> server.type().equals("proxy"))
                    .findFirst().orElse(null);

            ServerInfo gameServer = this.database.getAllEntries().stream()
                    .filter(server -> server.type().equals("server"))
                    .findFirst().orElse(null);

            if (proxyServer == null) {
                AtlasNetwork.logger.info("No Proxy Server found, creating a new one..");
                this.createProxy("AtlasNetwork - Proxy - 0");
            }

            if (gameServer == null) {
                AtlasNetwork.logger.info("No Game Server found, creating a new one..");
                this.createServer("AtlasNetwork - Server - 0");
            }
        } catch (DatabaseException e) {
            AtlasNetwork.logger.error("Could not validate existing servers..");
        }
    }

    public @Nullable ApplicationServer getServer(UUID serverId) {
        return this.servers.keySet().stream()
                .filter(server -> server.getUUID().equals(serverId))
                .findFirst()
                .orElse(null);
    }

    public ClientServer getClientServer(ApplicationServer server) {
        return this.servers.get(server);
    }

    public ApplicationServer createServer(EggConfig eggConfig, String name, boolean saveDatabase) throws PanelException {
        Node node = this.api.retrieveNodeById(eggConfig.resources().node()).execute();
        ApplicationEgg egg = this.api.retrieveEggById(this.api.retrieveNestById(eggConfig.nest()).execute(),
                eggConfig.egg()).execute();
        List<ApplicationAllocation> freeAlloc = node.retrieveAllocations().execute()
                .stream().filter(alloc -> !alloc.isAssigned()).toList();

        if (freeAlloc.size() < eggConfig.resources().allocations()) {
            throw new PanelNotEnoughAllocationException("Missing " + (eggConfig.resources().allocations() - freeAlloc.size()) + " allocations");
        }

        ServerCreationAction creationAction = this.api.createServer()
                .setCPU(eggConfig.resources().cpu())
                .setMemory(eggConfig.resources().memory(), DataType.MB)
                .setSwap(eggConfig.resources().swap(), DataType.MB)
                .setDisk(eggConfig.resources().disk(), DataType.MB)
                .setName(name)
                .setDescription("Server Generated by AtlasNetwork")
                .setOwner(this.api.retrieveUserById(this.config.userId()).execute())
                .startOnCompletion(true)
                .setEgg(egg)
                .setDockerImage(eggConfig.image());

        if (eggConfig.resources().allocations() > 1) {
            for (int i = 0; i < eggConfig.resources().allocations(); i++) {
                creationAction.setAllocation(freeAlloc.get(i));
            }
        } else {
            creationAction.setAllocation(freeAlloc.get(0));
        }

        Set<Map.Entry<String, JsonElement>> properties = eggConfig.variables().entrySet();
        if (!properties.isEmpty()) {
            Map<String, EnvironmentValue<?>> envProps = properties.stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> EnvironmentValue.of(entry.getValue().getAsString())
                    ));

            creationAction.setEnvironment(envProps);
        }

        ApplicationServer server = creationAction.execute();
        this.servers.put(server, this.client.retrieveServerByIdentifier(server.getIdentifier()).execute());
        if (saveDatabase) {
            try {
                this.database.save(new ServerInfo(server.getUUID().toString(), "egg"));
            } catch (DatabaseException e) {
                throw new PanelException("Could not save server to database", e);
            }
        }

        return server;
    }

    public ApplicationServer createServer(String name) throws PanelException {
        EggConfig egg = this.config.eggs().stream()
                .filter(eggFilter -> eggFilter.key().equals(this.config.defaults().server()))
                .findFirst().orElse(null);

        if (egg == null) {
            throw new PanelException("Cannot find defined default server egg '" + this.config.defaults().server() + "'!");
        }

        ApplicationServer server = this.createServer(egg, name, false);

        try {
            this.database.save(new ServerInfo(server.getUUID().toString(), "server"));
        } catch (DatabaseException e) {
            throw new PanelException("Could not save server to database", e);
        }

        return server;
    }

    public ApplicationServer createProxy(String name) throws PanelException {
        EggConfig egg = this.config.eggs().stream()
                .filter(eggFilter -> eggFilter.key().equals(this.config.defaults().proxy()))
                .findFirst().orElse(null);

        if (egg == null) {
            throw new PanelException("Cannot find defined default proxy egg '" + this.config.defaults().server() + "'!");
        }

        ApplicationServer server = this.createServer(egg, name, false);

        try {
            this.database.save(new ServerInfo(server.getUUID().toString(), "proxy"));
        } catch (DatabaseException e) {
            throw new PanelException("Could not save server to database", e);
        }

        return server;
    }

    public void deleteServer(UUID id) throws PanelException {
        ApplicationServer server = this.getServer(id);
        if (server == null) {
            throw new PanelServerNotExistException("Server does not exist!");
        }

        this.servers.get(server).stop().execute();

        try {
            this.database.remove(id.toString());
        } catch (DatabaseException e) {
            throw new PanelException("Could not delete server entry from database", e);
        }
        this.servers.remove(server);
        server.getController().delete(false).execute();
    }

    public PteroApplication getApi() {
        return api;
    }

    public PteroClient getClient() {
        return client;
    }

    public PanelConfig getConfig() {
        return config;
    }

    public Map<ApplicationServer, ClientServer> getServers() {
        return servers;
    }
}
