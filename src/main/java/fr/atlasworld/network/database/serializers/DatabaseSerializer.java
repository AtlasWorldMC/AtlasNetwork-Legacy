package fr.atlasworld.network.database.serializers;

import org.bson.Document;

public interface DatabaseSerializer<E> {
    E deserialize(Document document);
    Document serialize(E obj);
}
