package com.uit.gamestore.ui.login;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.uit.gamestore.R;
import com.uit.gamestore.domain.model.Result;
import com.uit.gamestore.domain.model.User;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        var viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        viewModel.getPasswordVisible().observe(this, this::onPasswordVisibleChanged);

        viewModel.getLoginResult().observe(this, this::onLoginComplete);

        Button loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(view -> viewModel.login());
    }

    private void onLoginComplete(Result<User> userResult) {
        if (userResult instanceof Result.Error) {
            var error = (Result.Error<User>) userResult;
            var message = error.getMessage();
            Log.e("LoginActivity", message);
        } else {
            var user = ((Result.Success<User>) userResult).getValue();
            Log.d("LoginActivity", user.toString());
            //TODO: Navigate to main screen
        }
    }

    private void onPasswordVisibleChanged(boolean isVisible) {
        EditText passwordEditText = findViewById(R.id.editText_password);
        if (isVisible) {
            passwordEditText.setTransformationMethod(null);
        } else {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
