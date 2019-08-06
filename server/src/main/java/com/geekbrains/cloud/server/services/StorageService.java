package com.geekbrains.cloud.server.services;

import com.geekbrains.cloud.common.AuthMessage;
import com.geekbrains.cloud.common.FilePathMessage;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StorageService {

    public ArrayList<FilePathMessage> sendStorageFiles(Path currentPath) throws IOException {
        ArrayList<FilePathMessage> pathList = new ArrayList<>();
        Path path = Paths.get(currentPath.toString());
        if (path.getParent() == null) {
            pathList.add(new FilePathMessage("rootDirectory", true));
        } else {
            pathList.add(new FilePathMessage(path.getParent().toString(), true));                   // записываем адрес родительского каталога
        }
        pathList.add(new FilePathMessage(path.toString(), true));                                   // записываем адрес текущего каталога
        ArrayList<Path> list = (ArrayList<Path>) Files.list(currentPath).collect(Collectors.toList());
        for (Path p : list) {
            if (Files.isDirectory(p)) {
                pathList.add(new FilePathMessage(p.toString(), true));                              // записываем адрес содержащихся папок в текущем каталоге
            } else {
                pathList.add(new FilePathMessage(p.toString(), false));                             // записываем адрес содержащихся файлов в текущем каталоге
            }
        }
        return pathList;
    }

    public static void createUserRootDirectory(String userName) throws IOException {
        Path rootPath = Paths.get("server_storage/" + userName);
        if (!Files.exists(rootPath)) {
            Files.createDirectory(rootPath);
        }
    }
}
