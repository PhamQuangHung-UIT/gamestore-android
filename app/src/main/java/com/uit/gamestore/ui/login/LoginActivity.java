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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.uit.gamestore.MainActivity;
import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.LoginResponse;
import com.uit.gamestore.domain.model.Result;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private LoginViewModel viewModel;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_password);
        loginButton = findViewById(R.id.button_login);
    }

    private void setupObservers() {
        viewModel.getPasswordVisible().observe(this, this::onPasswordVisibleChanged);
        viewModel.getLoginResult().observe(this, this::onLoginComplete);
        viewModel.getIsLoading().observe(this, this::onLoadingChanged);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(view -> viewModel.login());

        CheckBox passwordToggleButton = findViewById(R.id.button_passwordToggle);
        passwordToggleButton.setOnClickListener(view -> viewModel.togglePasswordVisible());

        emailEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setEmail(editable.toString());
            }
        });

        passwordEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setPassword(editable.toString());
            }
        });
    }

    private void onLoadingChanged(Boolean isLoading) {
        if (isLoading != null) {
            loginButton.setEnabled(!isLoading);
            loginButton.setText(isLoading ? R.string.loading : R.string.button_login);
        }
    }

    private void onLoginComplete(Result<LoginResponse> result) {
        if (result == null) return;

        if (result instanceof Result.Error) {
            Result.Error<LoginResponse> error = (Result.Error<LoginResponse>) result;
            String message = error.getMessage();
            Log.e(TAG, message != null ? message : "Unknown error");
            Snackbar.make(loginButton, message != null ? message : "Login failed", Snackbar.LENGTH_SHORT).show();
        } else if (result instanceof Result.Success) {
            LoginResponse response = ((Result.Success<LoginResponse>) result).getValue();
            if (response != null && response.getCustomer() != null) {
                Log.d(TAG, "Login successful: " + response.getCustomer().getUsername());
            } else {
                Log.d(TAG, "Login successful");
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void onPasswordVisibleChanged(Boolean isVisible) {
        if (isVisible == null) return;

        if (isVisible) {
            passwordEditText.setTransformationMethod(null);
        } else {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        passwordEditText.setSelection(passwordEditText.length());
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
