package com.geekbrains.cloud.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage mainStage;
    public static LoginController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        mainStage = primaryStage;
        primaryStage.setTitle("CLOUD STORAGE Client");
        mainStage.setScene(new Scene(root, 600, 400));
        mainStage.show();
        MessageHandler.getMessageHandler().run();
        MessageHandler.getMessageHandler().setLoginController(controller);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
