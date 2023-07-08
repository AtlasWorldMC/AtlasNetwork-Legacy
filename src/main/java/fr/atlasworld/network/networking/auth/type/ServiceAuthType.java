package fr.atlasworld.network.networking.auth.type;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.database.DatabaseDefiner;
import fr.atlasworld.network.networking.PacketByteBuf;
import fr.atlasworld.network.networking.auth.AuthResponses;
import fr.atlasworld.network.networking.auth.AuthManager;
import org.bson.Document;

public class ServiceAuthType implements AuthType{
    private final MongoClient mongoClient;

    public ServiceAuthType(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public AuthTypeResults authenticate(PacketByteBuf data) {
        String uniqueIdentifier = data.readString();
        String authToken = data.readString();

        MongoDatabase internalDatabase = this.mongoClient.getDatabase(DatabaseDefiner.INTERNAL_DATABASE);
        MongoCollection<Document> userCollection = internalDatabase.getCollection(DatabaseDefiner.INTERNAL_USERS_COLLECTION);

        Document serviceUserDoc = userCollection.find(Filters.and(
                Filters.eq("type", "service"),
                Filters.eq("uuid", uniqueIdentifier)
        )).first();

        if (serviceUserDoc == null) {
            return new AuthTypeResults(false, AuthResponses.UNKNOWN_ACCOUNT, "");
        }

        String savedAuthToken = serviceUserDoc.getString("auth_token_hash");
        String hashedAuthToken = AuthManager.hashString(authToken, "SHA-256");

        if (!hashedAuthToken.equals(savedAuthToken)) {
            return new AuthTypeResults(false, AuthResponses.TOKEN_INVALID, "");
        }

        return new AuthTypeResults(true, AuthResponses.SUCCESS, "");
    }
}
