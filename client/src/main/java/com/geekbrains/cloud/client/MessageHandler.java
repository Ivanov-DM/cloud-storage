package com.geekbrains.cloud.client;

import com.geekbrains.cloud.common.AbstractMessage;
import com.geekbrains.cloud.common.AuthMessage;
import com.geekbrains.cloud.common.FileMessage;
import com.geekbrains.cloud.common.FilePathMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class MessageHandler {
    private MainController mainController;
    private LoginController loginController;
    private static MessageHandler messageHandler;

    private MessageHandler() {

    }

    public static MessageHandler getMessageHandler(){
        if (messageHandler == null) {
            messageHandler = new MessageHandler();
        }
        return messageHandler;
    }

    public MainController getController1(MainController mainController) {
        return this.mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void run() {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Object obj = Network.readObject();
                    if (obj instanceof AbstractMessage) {
                        if (obj instanceof AuthMessage) {
                            AuthMessage authMsg = (AuthMessage) obj;
                            if (loginController != null) {
                                loginController.login(authMsg);
                            }
                            System.out.println("Auth");
                        }
                        if (obj instanceof FileMessage) {
                            FileMessage fileMsg = (FileMessage) obj;
                            Files.write(Paths.get("client_storage/" + fileMsg.getFileName()), fileMsg.getData(), StandardOpenOption.CREATE);
                        }
                    }
                    if (obj instanceof ArrayList) {
                        ArrayList<FilePathMessage> arr = (ArrayList<FilePathMessage>) obj;
                        arr.stream().forEach(x -> System.out.println(x.getFileName()));
                            mainController.showFiles(arr);
                    }
                }
            } catch (ClassNotFoundException |
                    IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
