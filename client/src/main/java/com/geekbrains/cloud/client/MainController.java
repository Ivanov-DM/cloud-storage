package com.geekbrains.cloud.client;

import com.geekbrains.cloud.common.FileMessage;
import com.geekbrains.cloud.common.FilePathMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    VBox mainParent;

    @FXML
    GridPane filesPane;

    @FXML
    ListView<String> filesList;

    @FXML
    TextField fileName;

    @FXML
    Label directoryLable;

    public ArrayList<FilePathMessage> currentFilesList = new ArrayList<>();

    double dragDeltaX, dragDeltaY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MessageHandler.getMessageHandler().setMainController(this);
        initializeDoubleClickForFilesPane();
        initializeWindowDragAndDropForFilesPane();
        initializeDragAndDropForFilesPane();
        System.out.println("11111111");
        FilePathMessage message = new FilePathMessage("rootDirectory", true);
        Network.sendMsg(message);
    }

    public void initializeDoubleClickForFilesPane() {
//        filesPane.setPadding(new Insets(20));
//        filesPane.setHgap(10);
//        filesPane.setVgap(10);
        filesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    String fileName = filesList.getSelectionModel().getSelectedItem();
                    System.out.println(fileName);
                    if (fileName.equals("...back")) {
                        Network.sendMsg(currentFilesList.get(0));
                        System.out.println(currentFilesList.get(0).getFileName());
                    } else {
                        for (FilePathMessage file : currentFilesList) {
                            Path path = Paths.get(file.getFileName());
                            if (fileName.equals(path.getFileName().toString())) {
                                Network.sendMsg(file);
                                System.out.println(file.getFileName());
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void initializeWindowDragAndDropForFilesPane() {
        Platform.runLater(() -> {
            Stage stage = (Stage) (mainParent.getScene().getWindow());
            filesList.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    // record a delta distance for the drag and drop operation.
                    dragDeltaX = stage.getX() - mouseEvent.getScreenX();
                    dragDeltaY = stage.getY() - mouseEvent.getScreenY();
                }
            });
            filesList.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    stage.setX(mouseEvent.getScreenX() + dragDeltaX);
                    stage.setY(mouseEvent.getScreenY() + dragDeltaY);
                }
            });
        });
    }

    public void initializeDragAndDropForFilesPane() {
        filesList.setOnDragOver(event -> {
            if (event.getGestureSource() != filesList && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        filesList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File o : db.getFiles()) {
                    Path path = o.toPath();
                    try {
                        FileMessage fileMessage = new FileMessage(path, currentFilesList.get(1).getFileName());
                        Network.sendMsg(fileMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(o.getAbsolutePath());
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void refreshFilesList(ArrayList<FilePathMessage> list) {
        if (Platform.isFxApplicationThread()) {
            filesList.getItems().clear();
            showFilesOnFilesPane(list);
        } else {
            Platform.runLater(() -> {
                filesList.getItems().clear();
                showFilesOnFilesPane(list);
            });
        }
    }

    public void showFilesOnFilesPane(ArrayList<FilePathMessage> list) {
        directoryLable.setText(Paths.get(list.get(1).getFileName()).getFileName().toString());
        if (!list.get(0).getFileName().equals("rootDirectory")) {
            filesList.getItems().add("...back");
        }
        for (int i = 2; i < list.size(); i++) {
            Path path = Paths.get(list.get(i).getFileName());
            filesList.getItems().add(path.getFileName().toString());
        }
    }

    public void downloadFilesInStorage(ActionEvent actionEvent) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выбрать файл для сохранения");
        Path path = fc.showOpenDialog(Main.mainStage).toPath();
        FileMessage fileMessage = new FileMessage(path, currentFilesList.get(1).getFileName());
        System.out.println("File name: " + fileMessage.getFileName() + "Save directory name: " + fileMessage.getSaveDirectoryName());
        Network.sendMsg(fileMessage);
    }

    public void downloadFileInClientStorage(ActionEvent actionEvent) {
        String fileName = filesList.getSelectionModel().getSelectedItem();
        if (fileName != null) {
            for (FilePathMessage f : currentFilesList) {
                Path path = Paths.get(f.getFileName());
                if (!f.isDirectory() && fileName.equals(path.getFileName().toString())) {
                    Network.sendMsg(f);
                }
            }
        }
    }

    public void deleteFileInStorage(ActionEvent actionEvent) {
        String fileName = filesList.getSelectionModel().getSelectedItem();
        if (fileName != null) {
            for (FilePathMessage f : currentFilesList) {
                Path path = Paths.get(f.getFileName());
                if (!f.isDirectory() && fileName.equals(path.getFileName().toString())) {
                    f.setDelete(true);
                    Network.sendMsg(f);
                }
            }
        }
    }

    public void searchFile(ActionEvent actionEvent) {
    }

    public void showFiles(ArrayList<FilePathMessage> arr) {
        if (!filesList.getItems().isEmpty()) {
            currentFilesList.clear();
            currentFilesList.addAll(arr);
            refreshFilesList(currentFilesList);
        } else {
            currentFilesList.addAll(arr);
            Platform.runLater(() -> {
                showFilesOnFilesPane(currentFilesList);
            });
        }
    }
}
