package fr.atlasworld.network.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class, for file managing
 */
public class FileManager {
    /**
     * Gets the current working directory
     * @return current working directory
     */
    public static File getWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    /**
     * Gets a file in the current working directory
     * @param filename name of the file
     * @return file
     */
    public static File getWorkingDirectoryFile(String filename) {
        return new File(getWorkingDirectory(), filename);
    }

    /**
     * Gets the configuration file
     * @return configuration file
     */
    public static File getConfigDirectory() {
        return getWorkingDirectoryFile("configuration");
    }

    /**
     * Gets the server configurations directory
     * @return server configurations directory
     */
    public static File getServerSchemaDirectory() {
        return getWorkingDirectoryFile("servers");
    }

    /**
     * Archive a directory
     * @param sourceFolder directory to archive
     * @param out archived file output path
     * @throws IOException if something went wrong
     */
    public static void archive(File sourceFolder, String out) throws IOException {
        FileOutputStream outStream = new FileOutputStream(out);
        ZipOutputStream zipOutStream = new ZipOutputStream(outStream);

        File[] files = sourceFolder.listFiles();
        if (files == null || files.length < 1) {
            return;
        }

        for (File file : files) {
            archiveFile(file, file.getName(), zipOutStream);
        }

        zipOutStream.close();
        outStream.close();
    }

    /**
     * Archives and adds a file to a zip stream
     * @param file file to archive
     * @param zipEntryName file name in the zip
     * @param stream zip stream
     * @throws IOException if something went wrong
     */
    public static void archiveFile(File file, String zipEntryName, ZipOutputStream stream) throws IOException {
        if (file.isDirectory()) {
            if (zipEntryName.endsWith("/")) {
                stream.putNextEntry(new ZipEntry(zipEntryName));
            } else {
                stream.putNextEntry(new ZipEntry(zipEntryName + "/"));
            }

            stream.closeEntry();
            File[] files = file.listFiles();
            if (files != null && !(files.length < 1)) {
                for (File dirFile : files) {
                    archiveFile(dirFile, zipEntryName + "/" + file.getName(), stream);
                }
            }
            return;
        }

        FileInputStream fileStream = new FileInputStream(file);
        ZipEntry entry = new ZipEntry(zipEntryName);
        stream.putNextEntry(entry);

        byte[] buffer = new byte[1024];
        int i;
        while ((i = fileStream.read(buffer)) >= 0)
            stream.write(buffer, 0, i);

        fileStream.close();
    }
}
