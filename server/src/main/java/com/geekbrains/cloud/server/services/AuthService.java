package com.geekbrains.cloud.server.services;

import com.geekbrains.cloud.server.repositories.AuthRepository;

public class AuthService {
    AuthRepository authRepository;

    public AuthService() {
        authRepository = new AuthRepository();
    }

    public boolean authUser(String log, String pas) {
        return authRepository.authUser(log, pas);
    }

    public void addNewUser(String log, String pas) {
        authRepository.createNewUser(log, pas);
    }

}
