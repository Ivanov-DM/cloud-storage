package com.geekbrains.cloud.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String saveDirectoryName;
    private String fileName;
    private byte[] data;

    public FileMessage(Path path, String saveDirectoryName) throws IOException {
        fileName = path.getFileName().toString();
        data = Files.readAllBytes(path);
        this.saveDirectoryName = saveDirectoryName;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public String getSaveDirectoryName() {
        return saveDirectoryName;
    }
}
