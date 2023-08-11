package fr.atlasworld.network.database.mongo;

import com.google.gson.Gson;
import fr.atlasworld.network.database.DatabaseSerializer;
import org.bson.Document;

public class MongoDatabaseSerializer implements DatabaseSerializer<Document> {
    private final Gson gson;

    public MongoDatabaseSerializer(Gson gson) {
        this.gson = gson;
    }

    public MongoDatabaseSerializer() {
        this(new Gson());
    }

    @Override
    public Document serialize(Object obj) {
        String json = this.gson.toJson(obj);
        return Document.parse(json);
    }

    @Override
    public <S> S deserialize(Document data, Class<S> type) {
        String json = data.toJson();
        return this.gson.fromJson(json, type);
    }
}
