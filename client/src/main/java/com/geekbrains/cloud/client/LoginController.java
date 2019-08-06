package com.geekbrains.cloud.client;

import com.geekbrains.cloud.common.AuthMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    Label wrongAuth;

    @FXML
    VBox authParent;

    public void auth(ActionEvent actionEvent) throws IOException {
        AuthMessage authMsg = new AuthMessage(login.getText(), password.getText());
//        System.out.println(authMsg.getLogin() + ", " + authMsg.getPassword());
        Network.sendMsg(authMsg);
    }

    public void signIn(ActionEvent actionEvent) {
        System.out.println(Thread.currentThread().getName());
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/sign_in.fxml"));
            Scene scene = new Scene(root, 600, 400);
            ((Stage)authParent.getScene().getWindow()).setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMainWindow() {
        System.out.println(Thread.currentThread().getName());
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/main.fxml"));
            Scene scene = new Scene(root, 600, 400);
            Platform.runLater(() -> ((Stage)authParent.getScene().getWindow()).setScene(scene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wrongAuth.setVisible(false);
        wrongAuth.setManaged(false);
        initLoginField();
    }

    public void initLoginField() {
        login.setOnKeyPressed(event -> {
            wrongAuth.setVisible(false);
            wrongAuth.setManaged(false);
        });
    }

    public void login(AuthMessage authMsg) {
        showMainWindow();
    }
}
