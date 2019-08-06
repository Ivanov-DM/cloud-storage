package com.geekbrains.cloud.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {
    @FXML
    TextField userName;

    @FXML
    PasswordField password;

    @FXML
    VBox signInParent;

    public void signIn(ActionEvent actionEvent) {
        System.out.println(Thread.currentThread().getName());
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/auth.fxml"));
            Scene scene = new Scene(root, 600, 400);
            ((Stage) signInParent.getScene().getWindow()).setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
