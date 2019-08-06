package com.geekbrains.cloud.server.repositories;

import java.util.ArrayList;
import java.util.List;

public class AuthRepository {
    List<Client> clientList = new ArrayList<>();

    public AuthRepository() {
        for (int i = 0; i < 10; i++) {
            clientList.add(new Client("login" + i, "pass" + i));
        }
    }

    public void createNewUser(String log, String pas) {
        clientList.add(new Client(log, pas));
    }

    public boolean authUser(String log, String pas) {
        return clientList.stream().anyMatch(x -> x.login.equals(log) && x.password.equals(pas));
    }

    static class Client {
        private String login;
        private String password;

        public Client(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

