package com.geekbrains.cloud.common;

import java.nio.file.Path;
import java.util.List;

public class AuthMessage extends AbstractMessage {
    private String login;
    private String password;
    private boolean authStatus;

    public AuthMessage(String login, String password) {
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

    public boolean isAuthrized() {
        return authStatus;
    }

    public void setAuthStatus(boolean authStatus) {
        this.authStatus = authStatus;
    }
}
