package com.geekbrains.cloud.common;

public class FilePathMessage extends AbstractMessage {
    private String fileName;
    private boolean isDirectory;
    private boolean isDelete;

    public FilePathMessage(String fileName, boolean isDirectory) {
        this.fileName = fileName;
        this.isDirectory = isDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isDelete() {
        return isDelete;
    }
}
