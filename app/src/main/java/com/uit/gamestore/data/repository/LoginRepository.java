package com.uit.gamestore.data.repository;

import androidx.annotation.NonNull;

import com.uit.gamestore.data.local.MockUserAPI;
import com.uit.gamestore.domain.model.Result;
import com.uit.gamestore.domain.model.User;

public class LoginRepository {

    private final MockUserAPI api = MockUserAPI.getInstance();

    public Result<User> login(@NonNull String email, @NonNull String password) {
        return api.login(email, password);
    }
}
