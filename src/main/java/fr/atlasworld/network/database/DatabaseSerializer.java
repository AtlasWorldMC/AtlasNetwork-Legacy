package fr.atlasworld.network.database;

public interface DatabaseSerializer<T> {
    T serialize(Object obj);
    <S> S deserialize(T data, Class<S> type);
}
