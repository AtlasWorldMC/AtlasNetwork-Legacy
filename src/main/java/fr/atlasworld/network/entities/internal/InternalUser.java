package fr.atlasworld.network.entities.internal;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.database.DatabaseDefiner;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.UUID;

public class InternalUser {
    private final MongoCollection<Document> internalUserCollection;
    private final Document data;

    public InternalUser(MongoCollection<Document> internalUserCollection, Document data) {
        this.internalUserCollection = internalUserCollection;
        this.data = data;
    }

    public UUID getUUID() {
        return UUID.fromString(this.data.getString("uuid"));
    }

    public String getUsername() {
        return this.data.getString("username");
    }

    public void setUsername(String username) {
        this.data.put("username", username);
    }

    public UserType getUserType() {
        return UserType.valueOf(this.data.getString("type").toUpperCase());
    }

    public void setUserType(UserType userType) {
        this.data.put("type", userType.name().toLowerCase());
    }

    public String getRankId() {
        return this.data.getString("rank");
    }
    public InternalRank getRank(MongoClient client) {
        return InternalRank.getRankById(this.getRankId(), client);
    }

    public void setRank(InternalRank rank) {
        String id = rank.getData().getString("_id");
        this.data.put("rank", id);
    }

    public MongoCollection<Document> getInternalUserCollection() {
        return internalUserCollection;
    }

    public Document getData() {
        return data;
    }

    public void updateDatabase() {
        this.internalUserCollection.replaceOne(new Document("_id", this.data.get("_id")), this.data);
    }

    public static InternalRank getUserById(String id, MongoClient client) {
        MongoDatabase db = client.getDatabase(DatabaseDefiner.INTERNAL_DATABASE);
        MongoCollection<Document> userCollection = db.getCollection(DatabaseDefiner.INTERNAL_USERS_COLLECTION);

        Document userDoc = userCollection.find(Filters.eq("_id", new ObjectId(id))).first();

        if (userDoc != null) {
            return new InternalRank(userCollection, userDoc);
        }
        return null;
    }
}
