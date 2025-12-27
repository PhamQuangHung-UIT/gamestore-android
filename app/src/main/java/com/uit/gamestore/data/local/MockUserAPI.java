package com.uit.gamestore.data.local;

import androidx.annotation.NonNull;

import com.uit.gamestore.domain.model.Result;
import com.uit.gamestore.domain.model.User;

public class MockUserAPI {

    public Result<User> login(@NonNull String email, @NonNull String password) {
        if (email.equals("phamhung7102003@gmail.com") && password.equals("12345678")) {
            return new Result.Success<>(new User("123", "phamhung7102003@gmail.com", "John Doe", null, null));
        } else {
            return new Result.Error<>("Invalid email or password");
        }
    }
}
