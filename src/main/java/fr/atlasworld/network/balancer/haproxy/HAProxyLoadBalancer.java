package fr.atlasworld.network.balancer.haproxy;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import fr.atlasworld.network.balancer.LoadBalancer;
import fr.atlasworld.network.balancer.entities.BalanceServerEntry;
import fr.atlasworld.network.config.BalancerConfig;
import fr.atlasworld.network.exceptions.requests.RequestException;
import fr.atlasworld.network.request.Request;
import org.checkerframework.checker.units.qual.C;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HAProxyLoadBalancer implements LoadBalancer {
    //End Points
    private static final String CONFIG_SERVERS_ENDPOINT = "/v2/services/haproxy/configuration/servers";
    private static final String CONFIG_VERSION_ENDPOINT = "/v2/services/haproxy/configuration/version";

    private final String baseUrl;
    private final String serverBackendName;
    private final String credentials;
    private final Map<String, HAProxyServer> servers;
    private int configVersion;

    public HAProxyLoadBalancer(String baseUrl, String serverBackendName, String username, String password) throws RequestException {
        this.baseUrl = baseUrl;
        this.serverBackendName = serverBackendName;
        this.credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        this.configVersion = new Request<Integer>()
                .url(this.baseUrl + CONFIG_VERSION_ENDPOINT)
                .addHeader("Authorization", "Basic " + this.credentials)
                .builder(JsonElement::getAsInt)
                .execute();

        this.servers = new Request<Map<String ,HAProxyServer>>()
                .url(this.baseUrl + CONFIG_SERVERS_ENDPOINT)
                .addHeader("Authorization", "Basic " + this.credentials)
                .addQuery("backend", this.serverBackendName)
                .builder(json -> json.getAsJsonObject().getAsJsonArray("data")
                        .asList()
                        .stream()
                        .map(JsonElement::getAsJsonObject)
                        .map(jsonEntry -> new HAProxyServer(
                                jsonEntry.get("name").getAsString(),
                                new InetSocketAddress(jsonEntry.get("address").getAsString(), jsonEntry.get("port").getAsInt())))
                        .collect(Collectors.toMap(
                                HAProxyServer::name,
                                server -> server)))
                .execute();
        System.out.println(servers);
    }

    public HAProxyLoadBalancer(BalancerConfig config) throws RequestException {
        this(config.url(), config.backendName(), config.username(), config.password());
    }

    @Override
    public List<BalanceServerEntry> getServers() {
        return ImmutableList.copyOf(new ArrayList<>(this.servers.values()));
    }

    @Override
    public void addServer(String name, InetSocketAddress address) throws RequestException {
        if (this.servers.containsKey(name)) {
            throw new RequestException("Server '" + name + "' already exists!");
        }

        HAProxyServer server = new HAProxyServer(name, address);
        new Request<Void>()
                .url(this.baseUrl + CONFIG_SERVERS_ENDPOINT)
                .addHeader("Authorization", "Basic " + this.credentials)
                .addQuery("backend", this.serverBackendName)
                .addQuery("version", String.valueOf(this.configVersion))
                .post(RequestBody.create(
                        MediaType.parse("application/json"),
                        server.toJson()))
                .builder(json -> null)
                .execute();
        this.servers.put(name, server);
        this.configVersion++;
    }

    @Override
    public void removeServer(BalanceServerEntry entry) throws RequestException {
        this.removeServer(entry.name());
    }

    @Override
    public void removeServer(String entry) throws RequestException {
        new Request<Void>()
                .url(this.baseUrl + CONFIG_SERVERS_ENDPOINT + "/" + entry)
                .addHeader("Authorization", "Basic " + this.credentials)
                .addQuery("backend", this.serverBackendName)
                .addQuery("version", String.valueOf(this.configVersion))
                .delete()
                .builder(json -> null)
                .execute();
        this.servers.remove(entry);
        this.configVersion++;
    }
}
