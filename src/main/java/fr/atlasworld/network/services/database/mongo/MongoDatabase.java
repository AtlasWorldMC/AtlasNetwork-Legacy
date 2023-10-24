package fr.atlasworld.network.services.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import fr.atlasworld.network.services.database.Database;
import fr.atlasworld.network.services.database.DatabaseData;
import fr.atlasworld.network.services.database.DatabaseEntityFactory;
import fr.atlasworld.network.services.database.IDatabaseEntity;
import fr.atlasworld.network.services.database.exceptions.DatabaseDataParsingException;
import fr.atlasworld.network.services.database.exceptions.DatabaseException;
import fr.atlasworld.network.services.database.exceptions.DatabaseIOException;
import fr.atlasworld.network.services.database.exceptions.DatabaseTimeoutException;
import fr.atlasworld.network.services.database.exceptions.unchecked.DatabaseConnectionClosedException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MongoDatabase<T extends IDatabaseEntity<T>> implements Database<T> {
    private final MongoCollection<Document> collection;
    private final DatabaseEntityFactory<T> factory;

    public MongoDatabase(MongoCollection<Document> collection, DatabaseEntityFactory<T> factory) {
        this.collection = collection;
        this.factory = factory;
    }

    @Override
    public void save(T value) throws DatabaseException {
        try {
            Document document = this.parse(value);
            this.collection.insertOne(document);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Database request timed-out.");
        } catch (MongoSocketWriteException e) {
            throw new DatabaseIOException("Prematurely reached end of stream.");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending data.");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void update(T value) throws DatabaseException {
        try {
            Document document = this.parse(value);
            Bson filter = this.filter(value.id());
            this.collection.replaceOne(filter, document);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Database request timed-out.");
        } catch (MongoSocketWriteException e) {
            throw new DatabaseIOException("Prematurely reached end of stream.");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending data.");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID key) throws DatabaseException {
        try {
            this.collection.deleteOne(this.filter(key));
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Database request timed-out.");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream.");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending request.");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public @Nullable T load(UUID key) throws DatabaseException {
        try {
            Document doc = this.collection.find(this.filter(key)).first();
            if (doc == null) {
                return null;
            }
            return this.fromDocument(doc);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Database request timed-out.");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream.");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while retrieving data.");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public boolean has(UUID key) throws DatabaseException {
        return this.load(key) != null;
    }

    @Override
    public Set<T> getAll() throws DatabaseException {
        try {
            Set<T> entries = new HashSet<>();
            for (Document doc : this.collection.find()) {
                entries.add(this.fromDocument(doc));
            }
            return entries;
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Database request timed-out.");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while retrieving data");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private Bson filter(UUID id) {
        return Filters.eq("_id", new ObjectId(id.toString()));
    }

    private Document parse(IDatabaseEntity<T> entity) throws DatabaseDataParsingException {
        DatabaseData data = entity.asData();
        if (data.has("_id")) {
            throw new DatabaseDataParsingException("'_id' Property may not be override!");
        }

        Document document = new Document();
        document.put("_id", new ObjectId(entity.id().toString()));
        document.putAll(data.asMap());

        return document;
    }

    private T fromDocument(Document document) {
        ObjectId id = document.getObjectId("_id");

        document.remove("_id");
        return this.factory.create(new DatabaseData(document), UUID.fromString(id.toString()));
    }
}
