package fr.atlasworld.network.server.pterodactyl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
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
import fr.atlasworld.network.balancer.LoadBalancer;
import fr.atlasworld.network.config.PanelConfig;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.database.entities.server.DatabaseServer;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.exceptions.requests.RequestException;
import fr.atlasworld.network.file.FileManager;
import fr.atlasworld.network.file.loader.GsonFileLoader;
import fr.atlasworld.network.file.loader.JsonFileLoader;
import fr.atlasworld.network.server.ServerManager;
import fr.atlasworld.network.server.configuration.NodeBalancer;
import fr.atlasworld.network.server.configuration.ServerConfiguration;
import fr.atlasworld.network.server.configuration.ServerFileConfiguration;
import fr.atlasworld.network.server.entities.PanelServer;
import fr.atlasworld.network.server.listener.ServerSetupEventListener;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Server Manager implementation for Pterodactyl Panel
 * @see ServerManager
 */
public class PteroServerManager implements ServerManager {
    private final LoadBalancer loadBalancer;
    private final Map<String, ServerConfiguration> serverConfigurations;
    private final Map<String, ServerFileConfiguration> serverFileConfigurations;
    private final List<PanelServer> servers;
    private final Database<DatabaseServer> serverDatabase;
    private final Database<AuthenticationProfile> profileDatabase;
    private final PteroApplication application;
    private final PteroClient client;
    private final PanelConfig config;
    private ServerConfiguration proxyDefault;
    private ServerConfiguration serverDefault;

    public PteroServerManager(LoadBalancer loadBalancer, Map<String, ServerConfiguration> serverConfigurations, Map<String, ServerFileConfiguration> serverFileConfigurations, List<PanelServer> servers, Database<DatabaseServer> serverDatabase, Database<AuthenticationProfile> profileDatabase, PteroApplication application, PteroClient client, PanelConfig config) {
        this.loadBalancer = loadBalancer;
        this.serverConfigurations = serverConfigurations;
        this.serverFileConfigurations = serverFileConfigurations;
        this.servers = servers;
        this.serverDatabase = serverDatabase;
        this.profileDatabase = profileDatabase;
        this.application = application;
        this.client = client;
        this.config = config;
    }

    public PteroServerManager(Database<DatabaseServer> serverDatabase, PanelConfig config, LoadBalancer loadBalancer, Database<AuthenticationProfile> profileDatabase) {
        this(
                loadBalancer,
                new HashMap<>(),
                new HashMap<>(),
                new ArrayList<>(),
                serverDatabase,
                profileDatabase, PteroBuilder.createApplication(config.url(), config.token()),
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

        if (FileManager.getServerFileIndex().exists()) {
            JsonFileLoader serverFileIndexLoader = new JsonFileLoader(FileManager.getServerFileIndex());
            Gson gson = new Gson();
            serverFileIndexLoader.load().getAsJsonArray()
                    .asList()
                    .stream()
                    .map(jsonFileIndex -> gson.fromJson(jsonFileIndex, ServerFileConfiguration.class))
                    .forEach(fileIndex -> this.serverFileConfigurations.put(fileIndex.id(), fileIndex));
        }

        List<ApplicationServer> networkManagedServers = this.application.retrieveServers().execute().stream()
                .filter(server -> server.retrieveOwner().execute().getIdLong() == this.config.userId())
                .toList();
        try {
            for (ApplicationServer server : networkManagedServers) {
                ClientServer clientServer = this.client.retrieveServerByIdentifier(server.getIdentifier()).execute();
                Optional<DatabaseServer> optStoredServer = this.serverDatabase.getOptional(server.getUUID().toString());

                if (optStoredServer.isEmpty()) {
                    AtlasNetwork.logger.warn("Found unknown server '{}', Deleting server..", server.getName());
                    server.getController().delete(false).executeAsync();

                    if (this.profileDatabase.has(server.getUUID().toString())) {
                        this.profileDatabase.remove(server.getUUID().toString());
                    }

                    continue;
                }

                DatabaseServer storedServer = optStoredServer.get();
                Utilization serverUses = clientServer.retrieveUtilization().execute();
                if (serverUses.getState() == UtilizationState.OFFLINE || serverUses.getState() == UtilizationState.STOPPING) {
                    AtlasNetwork.logger.info("'{}' is offline, deleting unused server..", server.getName());
                    server.getController().delete(false).executeAsync();
                    this.serverDatabase.remove(storedServer.getId());

                    if (this.profileDatabase.has(server.getUUID().toString())) {
                        this.profileDatabase.remove(server.getUUID().toString());
                    }

                    continue;
                }

                if (!this.serverConfigurations.containsKey(storedServer.getType())) {
                    AtlasNetwork.logger.warn("Found '{}' with an out-dated configuration id '{}', deleting server..", server.getName(), storedServer.getId());
                    server.getController().delete(false).executeAsync();
                    this.serverDatabase.remove(storedServer.getId());

                    if (this.profileDatabase.has(server.getUUID().toString())) {
                        this.profileDatabase.remove(server.getUUID().toString());
                    }

                    continue;
                }

                ServerConfiguration configuration = this.serverConfigurations.get(storedServer.getType());

                PteroServer panelServer = new PteroServer(
                        clientServer,
                        server,
                        configuration,
                        storedServer);

                panelServer.addListener(new ServerSetupEventListener(
                        this.serverFileConfigurations.get(configuration.id()),
                        this.profileDatabase,
                        !configuration.equals(this.proxyDefault),
                        null));

                CompletableFuture.runAsync(() -> {
                    try {
                        AtlasNetwork.SOCKET_STARTED_LATCH.await();
                        panelServer.getClientServer().sendCommand("network reconnect").execute();
                        AtlasNetwork.logger.info("Sent reconnect request to {}", panelServer.name());
                    } catch (InterruptedException e) {
                        AtlasNetwork.logger.error("Could not wait on the socket!", e);
                    }
                });

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
            this.createDefaultServer();
        }

        PanelServer proxy = this.servers.stream()
                .filter(availableServer -> availableServer.getDatabaseServer().getType()
                        .equals(this.proxyDefault.id()))
                .findFirst().orElse(null);

        if (proxy == null) {
            AtlasNetwork.logger.info("No Proxy found, creating a new one..");
            this.createDefaultProxy();
        }
    }

    @Override
    public List<PanelServer> getServers() {
        return ImmutableList.copyOf(this.servers);
    }

    @Override
    public @Nullable PanelServer getServer(UUID id) {
        return this.servers.stream()
                .filter(server -> server.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public PanelServer createCustomServer(ServerConfiguration configuration, String name) throws PanelException {
        return this.createCustomServer(configuration, name, null);
    }

    @Override
    public PanelServer createCustomServer(ServerConfiguration configuration, String name, UUID requestId) throws PanelException {
        NodeBalancer balancer = configuration.node().getBalancer();
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
            this.serverDatabase.save(databaseServer);
        } catch (DatabaseException e) {
            throw new PanelException("Could not save server to database", e);
        }

        clientServer.getWebSocketBuilder().addEventListeners(new ServerSetupEventListener(
                this.serverFileConfigurations.get(configuration.id()),
                this.profileDatabase,
                !configuration.equals(this.proxyDefault), // Only notify proxies when it's not a proxy that's being created.
                requestId)
        ).build();

        PteroServer server = new PteroServer(clientServer, appServer, configuration, databaseServer);
        this.servers.add(server);

        return server;
    }

    @Override
    public PanelServer createDefaultServer() throws PanelException {
        return this.createDefaultServer(null);
    }

    @Override
    public PanelServer createDefaultServer(UUID requestId) throws PanelException {
        return this.createCustomServer(this.serverDefault, "server-" + new Random().nextInt(999), requestId);
    }

    @Override
    public PanelServer createDefaultProxy() throws PanelException {
        return this.createDefaultProxy(null);
    }

    @Override
    public PanelServer createDefaultProxy(UUID requestId) throws PanelException {
        PteroServer server = (PteroServer) this.createCustomServer(this.proxyDefault, "proxy-" + new Random().nextInt(999), requestId);
        try {
            this.loadBalancer.addServer(server.name(), server.address());
            return server;
        } catch (RequestException e) {
            AtlasNetwork.logger.error("Could not add proxy to load balancer!");
            server.delete();
            try {
                this.serverDatabase.remove(server.id().toString());
                this.servers.remove(server);
            } catch (DatabaseException ex) {
                throw new PanelException("Could not delete server from database", e);
            }
            throw new PanelException("Could not add proxy to load balancer", e);
        }
    }

    @Override
    public Map<String, ServerConfiguration> getServerConfigurations() {
        return ImmutableMap.copyOf(this.serverConfigurations);
    }

    @Override
    public @Nullable ServerConfiguration getConfiguration(String id) {
        return this.serverConfigurations.get(id);
    }

    @Override
    public ServerConfiguration defaultServerConfiguration() {
        return this.serverDefault;
    }

    @Override
    public ServerConfiguration defaultProxyConfiguration() {
        return this.proxyDefault;
    }
}
