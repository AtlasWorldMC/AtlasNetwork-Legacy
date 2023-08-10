package fr.atlasworld.network.old_networking.auth;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.database.DatabaseDefiner;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.networking.NetworkErrors;
import fr.atlasworld.network.networking.packet.PacketByteBuf;
import fr.atlasworld.network.utils.CryptoUtils;
import io.netty.channel.Channel;
import org.bson.Document;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class TokenAuthenticationManager implements AuthenticationManager {
    private final DatabaseManager dbManager;

    private TokenAuthenticationManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    //Authenticate new connection
    public AuthResult authenticate(Channel channel, PacketByteBuf buf) {
        AtlasNetwork.logger.info("Starting authentication for {}.", channel.remoteAddress());

        MongoDatabase internalDatabase = dbManager.getClient().getDatabase(DatabaseDefiner.INTERNAL_DATABASE);
        MongoCollection<Document> apiCollection = internalDatabase.getCollection(DatabaseDefiner.API_COLLECTION);

        //If the database has no api token
        if (apiCollection.countDocuments() < 1) {
            AtlasNetwork.logger.warn("No api docs found, allowing this connection.");

            String token = generateSecureToken();
            UUID tokenUuid = UUID.randomUUID();

            Document document = new Document();
            document.put("token", CryptoUtils.hashString(token, "sha256"));
            document.put("uuid", tokenUuid.toString());

            apiCollection.insertOne(document);
            return new AuthResult(true, "{\"token\": \"" + token + "\", \"uuid\": \"" + tokenUuid + "\"}");
        }

        String uuid = buf.readString();
        String token = buf.readString();
        String hashedToken = CryptoUtils.hashString(token, "sha256");

        Document apiDocument = apiCollection.find(Filters.eq("token", hashedToken)).first();

        if (apiDocument == null) {
            AtlasNetwork.logger.error("Cannot find token document '{}' for '{}'!", token, channel.remoteAddress());
            return new AuthResult(false, NetworkErrors.INVALID_TOKEN);
        }

        if (apiDocument.containsKey("uuid")) {
            if (!apiDocument.getString("uuid").equals(uuid)) {
                return new AuthResult(false, NetworkErrors.UNKNOWN_OR_MISSING_UUID);
            }
            return new AuthResult(true, "");
        }

        UUID generatedAuthUuid = UUID.randomUUID();

        //Update document
        apiDocument.put("uuid", generatedAuthUuid.toString());
        apiCollection.replaceOne(Filters.eq("_id", apiDocument.get("_id")), apiDocument);

        //Send response
        return new AuthResult(true, "{\"uuid\": \"" + generatedAuthUuid + "\"}");
    }

    public String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    //Static Fields
    private static TokenAuthenticationManager manager;

    public static TokenAuthenticationManager getManager() {
        if (manager == null) {
            manager = new TokenAuthenticationManager(DatabaseManager.getManager());
        }

        return manager;
    }

}
