package fr.atlasworld.network.entities.internal;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.database.DatabaseDefiner;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Set;

@SuppressWarnings("unchecked")
public class InternalRank {
    private final MongoCollection<Document> internalRankCollection;
    private final Document data;

    public InternalRank(MongoCollection<Document> internalRankCollection, Document data) {
        this.internalRankCollection = internalRankCollection;
        this.data = data;
    }

    public String getName() {
        return this.data.getString("name");
    }

    public void setName(String name) {
        this.data.put("name", name);
    }

    public Set<String> getPermission() {
        return this.data.get("permission", Set.class);
    }

    public boolean hasPermission(String permission) {
        return this.getPermission().contains(permission);
    }

    public void addPermission(String permission) {
        Set<String> permissions = getPermission();
        permissions.add(permission);
        this.data.put("permission", permissions);
    }

    public void removePermission(String permission) {
        Set<String> permissions = getPermission();
        permissions.remove(permission);
        this.data.put("permission", permissions);
    }

    public MongoCollection<Document> getInternalRankCollection() {
        return internalRankCollection;
    }

    public Document getData() {
        return data;
    }

    public void updateDatabase() {
        this.internalRankCollection.replaceOne(new Document("_id", this.data.get("_id")), this.data);
    }

    public static InternalRank getRankById(String id, MongoClient client) {
        MongoDatabase db = client.getDatabase(DatabaseDefiner.INTERNAL_DATABASE);
        MongoCollection<Document> rankCollection = db.getCollection(DatabaseDefiner.INTERNAL_RANK_COLLECTION);

        Document rankDoc = rankCollection.find(Filters.eq("_id", new ObjectId(id))).first();

        if (rankDoc != null) {
            return new InternalRank(rankCollection, rankDoc);
        }
        return null;
    }
}
