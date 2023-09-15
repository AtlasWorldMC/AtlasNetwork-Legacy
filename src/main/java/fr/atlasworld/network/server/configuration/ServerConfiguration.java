package fr.atlasworld.network.server.configuration;

import com.google.gson.JsonObject;

/**
 * Server configuration
 * @param id server id
 * @param version version
 * @param image docker image for the server
 * @param location remote installation script location
 * @param node defines which nodes the server can be created
 * @param resources defines the resources that the server is allowed to use
 * @param variables installation scripts variables
 */
public record ServerConfiguration(String id, String version, String image, Location location, Node node, Resources resources, JsonObject variables) {

    /**
     * Sets the location of the remote installation script
     * @param nest nest id of the egg
     * @param egg egg id
     */
    public record Location(int nest, int egg) {
    }

    /**
     * Sets the nodes on which the server is allowed to be created on, and defines the balancing algorithm
     * @param nodes nodes on which the server is allowed to be created on
     * @param balancer sets the balancing algorithm for selecting the node
     */
    public record Node(Integer[] nodes, String balancer){
        public NodeBalancer getBalancer() {
            return NodeBalancer.fromString(this.balancer);
        }
    }

    /**
     * Sets the resources for the server
     * @param allocation sets how much allocation the server needs to work, (Ex allocation: 127.0.0.1:25565)
     * @param memory sets how much memory the server is allowed to use in MB
     * @param swap sets how much swap is allocated to the server in MB
     * @param cpu sets how much cpu power the server is allowed to use
     * @param disk sets how much disk is allocated to the server in MB
     */
    public record Resources(int allocation, int memory, int swap, int cpu, int disk) {
    }
}
