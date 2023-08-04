package client;

import client.networking.ClientSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkClient {
    public static final Logger logger = LoggerFactory.getLogger("NetworkClient");
    public static void main(String[] args) {
        ClientSocket socket = new ClientSocket(27767, "127.0.0.1");
        socket.connect();
    }
}
