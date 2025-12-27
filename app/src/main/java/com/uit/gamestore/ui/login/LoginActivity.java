package com.uit.gamestore.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.uit.gamestore.MainActivity;
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

        CheckBox passwordToggleButton = findViewById(R.id.button_passwordToggle);

        passwordToggleButton.setOnClickListener(view -> viewModel.togglePasswordVisible());

        EditText emailEditText = findViewById(R.id.editText_email);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setEmail(editable.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        EditText passwordEditText = findViewById(R.id.editText_password);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setPassword(editable.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

    private void onLoginComplete(Result<User> userResult) {
        if (userResult instanceof Result.Error) {
            var error = (Result.Error<User>) userResult;
            var message = error.getMessage();
            Log.e("LoginActivity", message);
            Snackbar.make(findViewById(R.id.button_login), message, Snackbar.LENGTH_SHORT).show();
        } else {
            var user = ((Result.Success<User>) userResult).getValue();
            Log.d("LoginActivity", user.toString());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
