package fr.atlasworld.network.services.panel.pteroq;

import com.google.gson.JsonElement;
import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.*;
import com.mattmalec.pterodactyl4j.application.managers.ServerCreationAction;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.config.files.PanelConfiguration;
import fr.atlasworld.network.services.database.Database;
import fr.atlasworld.network.services.exception.ServiceException;
import fr.atlasworld.network.services.panel.PanelService;
import fr.atlasworld.network.services.panel.exceptions.unchecked.UnknownServerSchemaPanelException;
import fr.atlasworld.network.services.panel.pteroq.entities.PteroqServer;
import fr.atlasworld.network.services.panel.schema.ServerSchemaManager;
import fr.atlasworld.network.services.panel.data.ServerDatabase;
import fr.atlasworld.network.services.panel.entities.PanelServer;
import fr.atlasworld.network.services.panel.schema.ServerSchema;
import fr.atlasworld.network.services.panel.exceptions.PanelException;
import fr.atlasworld.network.services.panel.pteroq.exceptions.ServerScanningFailedException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PteroqPanelService implements PanelService {
    private final PteroClient client;
    private final PteroApplication application;
    private final PanelConfiguration configuration;

    private final Database<ServerDatabase> database;
    private final ServerSchemaManager schemaManager;

    private boolean started;

    public PteroqPanelService(PteroClient client, PteroApplication application, PanelConfiguration configuration, Database<ServerDatabase> database, ServerSchemaManager schemaManager) throws PanelException {
        this.client = client;
        this.application = application;
        this.configuration = configuration;
        this.database = database;
        this.schemaManager = schemaManager;

        this.started = false;
    }

    public PteroqPanelService(PanelConfiguration configuration, Database<ServerDatabase> database, ServerSchemaManager schemaManager) throws PanelException {
        this(PteroBuilder.createClient(configuration.url(), configuration.token()),
                PteroBuilder.createApplication(configuration.url(), configuration.token()), configuration, database, schemaManager);
    }

    @Override
    public void start() throws PanelException {
        List<ApplicationServer> availableServers = this.application.retrieveServers()
                .stream()
                .filter(server -> server.retrieveOwner().execute().getIdLong() == this.configuration.accountId())
                .toList();

        try {
            this.scanServers(availableServers);
        } catch (PanelException e) {
            throw e;
        } catch (ServiceException e) {
            throw new ServerScanningFailedException("Server Scanning Failed.", e);
        }

        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    private void scanServers(List<ApplicationServer> servers) throws ServiceException {
        AtlasNetwork.logger.info("[Pterodactyl-Panel-Service] Scanning servers..");

        for (ApplicationServer server : servers) {
            AtlasNetwork.logger.debug("[Pterodactyl-Panel-Service] Scanning on '{}' on remote..", server.getName());

            if (!this.database.has(server.getUUID())) {
                AtlasNetwork.logger.warn("[Pterodactyl-Panel-Service]'{}' could not be found in the database, deleting server..", server.getName());
                server.getController().delete(false).execute();
            }
        }

        for (ServerDatabase server : this.database.getAll()) {
            AtlasNetwork.logger.debug("[Pterodactyl-Panel-Service] Scanning on database '{}' on remote..", server.id());

            long matchingServersCount = servers.stream()
                    .filter(remoteServer -> remoteServer.getUUID().equals(server.id()))
                    .count();

            if (matchingServersCount > 1) {
                throw new ServerScanningFailedException("THIS SHOULD NOT BE POSSIBLE, MULTIPLE SERVERS FOUND WITH THE SAME ID! ID: " + server.id());
            }

            if (matchingServersCount == 0) {
                AtlasNetwork.logger.warn("[Pterodactyl-Panel-Service] Server '{}' could not be found on remote.", server.id());
                this.database.delete(server.id());
                return;
            }

            AtlasNetwork.logger.debug("[Pterodactyl-Panel-Service] Found match for '{}'", server.id());
        }

        AtlasNetwork.logger.info("[Pterodactyl-Panel-Service] Scanning finished.");
    }

    @Override
    public PanelServer createServer(String name, ServerSchema schema) throws PanelException {
        this.ensureStarted();

        AtlasNetwork.logger.debug("Finding suitable node for '{}'..", name);
        List<Node> nodes = this.application.retrieveNodes()
                .stream()
                .filter(node -> !this.configuration.nodeBlacklist().contains(node.getIdLong()))
                .toList();

        int lowestServerAmountIndex = -1;
        int lowestServerAmount = Integer.MAX_VALUE;
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            int nodeServerAmount = node.retrieveServers().execute().size();

            if (lowestServerAmount <= nodeServerAmount) {
                continue;
            }

            int allocationsAvailable = (int) node.retrieveAllocations()
                    .all()
                    .execute()
                    .stream()
                    .filter(alloc -> !alloc.isAssigned())
                    .count();

            if (allocationsAvailable < Math.max(schema.resources().allocations(), 1)) {
                AtlasNetwork.logger.warn("Panel Node '{}' does not have enough allocations for '{}'", node.getName(), schema.id());
                continue;
            }

            lowestServerAmount = nodeServerAmount;
            lowestServerAmountIndex = i;
        }

        if (lowestServerAmountIndex == -1) {
            throw new PanelException("Could not find a suitable node for new server '" + name + "' based of schema '" + schema.id() + "'!");
        }

        Node node = nodes.get(lowestServerAmountIndex);
        AtlasNetwork.logger.debug("Node '{}' has been selected for '{}'.", node.getName(), name);

        return this.createServer(name, schema, node);
    }

    @Override
    public PanelServer createServer(String name, String schemaId) throws PanelException {
        this.ensureStarted();

        ServerSchema schema = this.schemaManager.getSchema(schemaId);
        if (schema == null) {
            throw new UnknownServerSchemaPanelException("Could not find any schema with id '" + schemaId + "'!");
        }

        return this.createServer(name, schema);
    }

    private PanelServer createServer(String name, ServerSchema schema, Node node) throws PanelException {
        this.ensureStarted();

        ServerCreationAction creationAction = this.application.createServer();

        creationAction.setName(name)
                .setDescription("AtlasNetwork Generated Server.")
                .setOwner(this.application.retrieveUserById(this.configuration.accountId()).execute())
                .setCPU(schema.resources().cpu())
                .setMemory(schema.resources().memory(), DataType.MB)
                .setSwap(schema.resources().swap() == null ? schema.resources().memory() / 2 : schema.resources().swap(),
                        DataType.MB) // If the swap is not defined, by default get the configured memory and divide it by 2;
                .setDisk(schema.resources().disk(), DataType.MB);

        List<ApplicationAllocation> availableAllocations = node.retrieveAllocations()
                .all()
                .execute()
                .stream()
                .filter(alloc -> !alloc.isAssigned())
                .toList();

        ApplicationAllocation primaryAlloc = availableAllocations.get(0);

        if (schema.resources().allocations() > 1) {
            creationAction.setAllocations(primaryAlloc, availableAllocations.subList(1, schema.resources().allocations() - 1));
        } else {
            creationAction.setAllocations(primaryAlloc);
        }

        Nest nest = this.application.retrieveNestById(schema.egg().nest()).execute();
        if (nest == null) {
            throw new PanelException("Cannot find nest with id '" + schema.egg().nest() + "'!");
        }

        ApplicationEgg egg = this.application.retrieveEggById(nest, schema.egg().egg()).execute();
        if (egg == null) {
            throw new PanelException("Cannot find egg with id '" + schema.egg().egg() + "'!");
        }

        creationAction.setEgg(egg);

        if (schema.misc() != null) {
            if (schema.misc().image() != null) {
                creationAction.setDockerImage(schema.misc().image());
            }

            if (schema.misc().launch() != null) {
                creationAction.setStartupCommand(schema.misc().launch());
            }
        }

        Map<String, EnvironmentValue<?>> envVariables = schema.variables()
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), EnvironmentValue.of(entry.getValue().getAsString())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        creationAction.setEnvironment(envVariables);

        ApplicationServer server = creationAction.execute();

        return new PteroqServer(server, schema);
    }

    private void ensureStarted() {
        if (!this.started) {
            throw new UnsupportedOperationException("Panel service not started!");
        }
    }
}
