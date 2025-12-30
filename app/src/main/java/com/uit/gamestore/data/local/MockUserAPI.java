package com.uit.gamestore.data.local;

import androidx.annotation.NonNull;

import com.uit.gamestore.domain.model.Result;
import com.uit.gamestore.domain.model.User;

import java.util.ArrayList;

public class MockUserAPI {

    private static MockUserAPI instance;

    private MockUserAPI() {}

    public static MockUserAPI getInstance() {
        if (instance == null) {
            instance = new MockUserAPI();
        }
        return instance;
    }

    public Result<User> login(@NonNull String email, @NonNull String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return new Result.Error<>("Email and password are required");
        }
        if (email.equals("test@example.com") && password.equals("12345678")) {
            return new Result.Success<>(new User("123", "test@example.com", "John Doe", new ArrayList<>(), new ArrayList<>()));
        } else {
            return new Result.Error<>("Invalid email or password");
        }
    }
}
