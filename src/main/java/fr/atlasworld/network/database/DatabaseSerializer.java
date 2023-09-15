package fr.atlasworld.network.database;

/**
 * DatabaseSerializer Interface, used for transforming raw data into java objects and back to raw data
 * @param <T> raw data type (ex: MongoDb uses Document)
 */
public interface DatabaseSerializer<T> {
    T serialize(Object obj);
    <S> S deserialize(T data, Class<S> type);
}
