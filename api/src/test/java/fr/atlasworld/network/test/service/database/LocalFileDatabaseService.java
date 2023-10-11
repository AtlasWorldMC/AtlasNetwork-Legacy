package fr.atlasworld.network.test.service.database;

import fr.atlasworld.network.api.NetworkEntity;
import fr.atlasworld.network.api.exception.services.database.DatabaseException;
import fr.atlasworld.network.api.file.loader.JsonFileLoader;
import fr.atlasworld.network.api.services.database.Database;
import fr.atlasworld.network.api.services.database.DatabaseService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

public class LocalFileDatabaseService implements DatabaseService {
    private static final File dataDirectory = new File("/data/database/");

    private boolean closed = false;

    @Override
    public <T extends NetworkEntity> Database<T> getDatabase(String name, Type type) throws DatabaseException {
        this.ensureNotClosed();

        File databaseFile = new File(dataDirectory, name);

        if (!databaseFile.isFile()) {
            dataDirectory.mkdirs();
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                throw new DatabaseException("Unable to create database file: ", e);
            }
        }

        return new LocalFileDatabase<>(databaseFile, type);
    }

    @Override
    public void closeConnection() throws DatabaseException {
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }
}
