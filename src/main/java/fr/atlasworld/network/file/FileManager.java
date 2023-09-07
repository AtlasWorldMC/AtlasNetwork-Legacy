package fr.atlasworld.network.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManager {
    //Static Fields
    public static final String CONFIG = "config.json";

    public static File getWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    public static File getWorkingDirectoryFile(String filename) {
        return new File(getWorkingDirectory(), filename);
    }

    public static File getConfigFile() {
        return getWorkingDirectoryFile(CONFIG);
    }

    public static File getServerConfigDirectory() {
        return getWorkingDirectoryFile("servers");
    }

    public static File getServerFileIndex() {
        return getWorkingDirectoryFile("hub/index.json");
    }

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
