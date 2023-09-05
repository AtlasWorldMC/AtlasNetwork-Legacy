package fr.atlasworld.network.server.pterodactyl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.UtilizationState;
import com.mattmalec.pterodactyl4j.application.entities.*;
import com.mattmalec.pterodactyl4j.application.managers.ServerCreationAction;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import com.mattmalec.pterodactyl4j.client.entities.Utilization;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.PanelConfig;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.server.DatabaseServer;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.configuration.NodeBalancer;
import fr.atlasworld.network.server.configuration.ServerConfiguration;
import fr.atlasworld.network.server.entities.PanelServer;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PteroServerManager implements ServerManager {
    private final Map<String, ServerConfiguration> serverConfigurations;
    private final List<PanelServer> servers;
    private final Database<DatabaseServer> database;
    private final PteroApplication application;
    private final PteroClient client;
    private final PanelConfig config;
    private ServerConfiguration proxyDefault;
    private ServerConfiguration serverDefault;

    public PteroServerManager(Map<String, ServerConfiguration> serverConfigurations, List<PanelServer> servers, Database<DatabaseServer> database, PteroApplication application, PteroClient client, PanelConfig config) {
        this.serverConfigurations = serverConfigurations;
        this.servers = servers;
        this.database = database;
        this.application = application;
        this.client = client;
        this.config = config;
    }

    public PteroServerManager(Database<DatabaseServer> database, PanelConfig config) {
        this(
                new HashMap<>(),
                new ArrayList<>(),
                database,
                PteroBuilder.createApplication(config.url(), config.token()),
                PteroBuilder.createClient(config.url(), config.token()),
                config
        );
    }

    @Override
    public void initialize() throws PanelException {
        File serverConfigurationsDirectory = FileManager.getServerConfigDirectory();
        File[] serverConfigurationsFiles = serverConfigurationsDirectory.listFiles(file ->
                file.isFile() && file.getName().endsWith(".json"));

        if (serverConfigurationsFiles == null || serverConfigurationsFiles.length < 1) {
            AtlasNetwork.logger.warn("No server configurations found, AtlasNetwork will not be able to create servers automatically");
        } else {
            for (File serverConfig : serverConfigurationsFiles) {
                GsonFileLoader<ServerConfiguration> configLoader = new GsonFileLoader<>(serverConfig, ServerConfiguration.class);
                ServerConfiguration configuration = configLoader.load();
                if (serverConfigurations.containsKey(configuration.id())) {
                    AtlasNetwork.logger.warn("Found multiple '{}' skipping {}", configuration.id(), serverConfig.getName());
                    continue;
                }
                this.serverConfigurations.put(configuration.id(), configuration);
            }
        }

        List<ApplicationServer> networkManagedServers = this.application.retrieveServers().execute().stream()
                .filter(server -> server.retrieveOwner().execute().getIdLong() == this.config.userId())
                .toList();
        try {
            for (ApplicationServer server : networkManagedServers) {
                ClientServer clientServer = this.client.retrieveServerByIdentifier(server.getIdentifier()).execute();
                Optional<DatabaseServer> optStoredServer = this.database.getOptional(server.getUUID().toString());

                if (optStoredServer.isEmpty()) {
                    AtlasNetwork.logger.warn("Found unknown server '{}', Deleting server..", server.getName());
                    server.getController().delete(false).executeAsync();
                    continue;
                }

                DatabaseServer storedServer = optStoredServer.get();
                Utilization serverUses = clientServer.retrieveUtilization().execute();
                if (serverUses.getState() == UtilizationState.OFFLINE || serverUses.getState() == UtilizationState.STOPPING) {
                    AtlasNetwork.logger.info("'{}' is offline, deleting unused server..", server.getName());
                    server.getController().delete(false).executeAsync();
                    this.database.remove(storedServer.getId());
                    continue;
                }

                if (!this.serverConfigurations.containsKey(storedServer.getType())) {
                    AtlasNetwork.logger.warn("Found '{}' with an out-dated configuration id '{}', deleting server..", server.getName(), storedServer.getId());
                    server.getController().delete(false).executeAsync();
                    this.database.remove(storedServer.getId());
                    continue;
                }

                PteroServer panelServer = new PteroServer(
                        clientServer,
                        server,
                        this.serverConfigurations.get(storedServer.getId()),
                        storedServer);

                this.servers.add(panelServer);
            }
        } catch (DatabaseException e) {
            throw new PanelException("Could not load servers", e);
        }

        //Get the default configurations
        this.serverDefault = this.serverConfigurations.get(this.config.defaults().server());
        if (this.serverDefault == null) {
            throw new PanelException("Cannot find default server configuration '" + this.config.defaults().server() + "'!");
        }

        this.proxyDefault = this.serverConfigurations.get(this.config.defaults().proxy());
        if (this.proxyDefault == null) {
            throw new PanelException("Cannot find default proxy configuration '" + this.config.defaults().server() + "'!");
        }

        //Checking default servers
        PanelServer server = this.servers.stream()
                .filter(availableServer -> availableServer.getDatabaseServer().getType()
                        .equals(this.serverDefault.id()))
                .findFirst().orElse(null);

        if (server == null) {
            AtlasNetwork.logger.info("No Server found, creating a new one..");
            this.createDefaultServer("Server - 0");
        }

        PanelServer proxy = this.servers.stream()
                .filter(availableServer -> availableServer.getDatabaseServer().getType()
                        .equals(this.proxyDefault.id()))
                .findFirst().orElse(null);

        if (proxy == null) {
            AtlasNetwork.logger.info("No Proxy found, creating a new one..");
            this.createDefaultProxy("Proxy - 0");
        }
    }

    @Override
    public List<PanelServer> getServers() {
        return ImmutableList.copyOf(this.servers);
    }

    @Override
    public PanelServer createCustomServer(ServerConfiguration configuration, String name) throws PanelException {
        NodeBalancer balancer = NodeBalancer.fromString(configuration.node().balancer());
        int nodeId = configuration.node().nodes()[balancer.selector(configuration.node().nodes())];

        Node node = this.application.retrieveNodeById(nodeId).execute();
        List<ApplicationAllocation> freeAllocations = node.retrieveAllocations().stream()
                .filter(alloc -> !alloc.isAssigned()).toList();

        if (freeAllocations.size() < configuration.resources().allocation()) {
            throw new PanelException("Not enough allocations available");
        }

        Nest nest = this.application.retrieveNestById(configuration.location().nest()).execute();
        ApplicationEgg egg = this.application.retrieveEggById(nest, configuration.location().egg()).execute();

        ServerCreationAction createAction = this.application.createServer()
                .setName(name)
                .setDescription("Generated by AtlasNetwork")
                .setOwner(this.application.retrieveUserById(this.config.userId()).execute())
                .setCPU(configuration.resources().cpu())
                .setDisk(configuration.resources().disk(), DataType.MB)
                .setMemory(configuration.resources().memory(), DataType.MB)
                .setSwap(configuration.resources().swap(), DataType.MB)
                .setDockerImage(configuration.image())
                .setEgg(egg);

        if (configuration.resources().allocation() > 1) {
            for (int i = 0; i < configuration.resources().allocation(); i++) {
                createAction.setAllocation(freeAllocations.get(i));
            }
        } else {
            createAction.setAllocation(freeAllocations.get(0));
        }

        Map<String, EnvironmentValue<?>> variableEntries = configuration.variables().asMap()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> EnvironmentValue.of(entry.getValue().getAsString()))
                );

        createAction.setEnvironment(variableEntries);

        ApplicationServer appServer = createAction.execute();
        ClientServer clientServer = this.client.retrieveServerByIdentifier(appServer.getIdentifier()).execute();
        DatabaseServer databaseServer = new DatabaseServer(appServer.getUUID(), configuration.id());

        try {
            this.database.save(databaseServer);
        } catch (DatabaseException e) {
            throw new PanelException("Could not save server to database", e);
        }

        return new PteroServer(clientServer, appServer, configuration, databaseServer);
    }

    @Override
    public PanelServer createDefaultServer(String name) throws PanelException {
        return this.createCustomServer(this.serverDefault, name);
    }

    @Override
    public PanelServer createDefaultProxy(String name) throws PanelException {
        return this.createCustomServer(this.proxyDefault, name);
    }

    @Override
    public Map<String, ServerConfiguration> getServerConfigurations() {
        return ImmutableMap.copyOf(this.serverConfigurations);
    }
}
