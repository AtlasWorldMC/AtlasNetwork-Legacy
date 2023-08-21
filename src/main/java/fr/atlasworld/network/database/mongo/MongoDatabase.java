package fr.atlasworld.network.database.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.DatabaseSerializer;
import fr.atlasworld.network.exceptions.database.DatabaseConnectionClosedException;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.database.DatabaseIOException;
import fr.atlasworld.network.exceptions.database.DatabaseTimeoutException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashSet;
import java.util.Set;

public class MongoDatabase<T> implements Database<T> {
    private final Class<T> clazz;
    private final MongoCollection<Document> collection;
    private final Function<String, Bson> finder;
    private final DatabaseSerializer<Document> serializer;

    public MongoDatabase(Class<T> clazz, MongoCollection<Document> collection, Function<String, Bson> finder, DatabaseSerializer<Document> serializer) {
        this.clazz = clazz;
        this.collection = collection;
        this.finder = finder;
        this.serializer = serializer;
    }

    @Override
    public T get(String id) throws DatabaseException {
        try {
            Document doc = this.collection.find(this.finder.apply(id)).first();
            if (doc == null) {
                return null;
            }
            return this.serializer.deserialize(doc, this.clazz);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not retrieve data from database");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while retrieving data");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public boolean has(String id) throws DatabaseException {
        return this.get(id) != null;
    }

    @Override
    public void save(T value) throws DatabaseException {
        try {
            Document doc = this.serializer.serialize(value);
            this.collection.insertOne(doc);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not send data to database");
        } catch (MongoSocketWriteException e) {
            throw new DatabaseIOException("Prematurely reached end of stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending data");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void update(T value, String id) throws DatabaseException {
        try {
            Document doc = this.serializer.serialize(value);
            this.collection.replaceOne(this.finder.apply(id), doc);
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not send data to database");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending data");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void remove(String id) throws DatabaseException {
        try {
            this.collection.deleteOne(this.finder.apply(id));
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not send request to database");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while sending request");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public Set<T> getAllEntries() throws DatabaseException {
        try {
            Set<T> entries = new HashSet<>();
            for (Document doc : this.collection.find()) {
                entries.add(this.serializer.deserialize(doc, this.clazz));
            }
            return entries;
        } catch (MongoTimeoutException e) {
            throw new DatabaseTimeoutException("Could not retrieve data from database");
        } catch (MongoSocketReadException e) {
            throw new DatabaseIOException("Prematurely reached end of stream");
        } catch (MongoSocketClosedException e) {
            throw new DatabaseConnectionClosedException("Connection was terminated before or while retrieving data");
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
