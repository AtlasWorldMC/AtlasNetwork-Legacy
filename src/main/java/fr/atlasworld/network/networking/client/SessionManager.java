package fr.atlasworld.network.networking.client;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.database.DatabaseDefiner;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.internal.InternalUser;
import fr.atlasworld.network.networking.Client;
import io.netty.channel.Channel;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;

public class SessionManager {
    public final HashMap<Channel, Client> sessionHolder;
    public final MongoClient dbClient;

    private SessionManager(HashMap<Channel, Client> sessionHolder, MongoClient dbClient) {
        this.sessionHolder = sessionHolder;
        this.dbClient = dbClient;
    }

    public Client getClientForSessions(Channel channel) {
        return sessionHolder.get(channel);
    }

    public void createSession(Channel channel, String docId) {
        if (this.sessionHolder.containsKey(channel)) {
            AtlasNetwork.logger.warn("{} was registered multiple times", channel.remoteAddress());
            return;
        }

        MongoDatabase database = dbClient.getDatabase(DatabaseDefiner.INTERNAL_DATABASE);
        MongoCollection<Document> userCollection = database.getCollection(DatabaseDefiner.INTERNAL_USERS_COLLECTION);

        Document userDocument = userCollection.find(Filters.eq("_id", new ObjectId(docId))).first();

        if (userDocument == null) {
            throw new IllegalStateException("Cannot find user document for " + channel.remoteAddress());
        }

        Client client = new Client(channel, new InternalUser(userCollection, userDocument));

        this.sessionHolder.put(channel, client);
    }

    public void deleteSession(Channel channel) {
        this.sessionHolder.remove(channel);
    }

    public boolean containsSession(Channel channel) {
        return this.sessionHolder.containsKey(channel);
    }

    //Static fields
    public static SessionManager manager;

    public static SessionManager getManager() {
        if(manager == null) {
            manager = new SessionManager(new HashMap<>(), DatabaseManager.getManager().getClient());
        }

        return manager;
    }
}
