package com.uit.gamestore.ui.register;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.LoginResponse;
import com.uit.gamestore.data.repository.AuthRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;

    private final AuthRepository authRepository = new AuthRepository();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.editText_username);
        emailEditText = findViewById(R.id.editText_email);
        phoneEditText = findViewById(R.id.editText_phone);
        passwordEditText = findViewById(R.id.editText_password);
        confirmPasswordEditText = findViewById(R.id.editText_confirmPassword);
        registerButton = findViewById(R.id.button_register);
        loginTextView = findViewById(R.id.textView_login);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> attemptRegister());
        loginTextView.setOnClickListener(v -> {
            finish();
        });
    }

    private void attemptRegister() {
        if (isLoading) return;

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Validation
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        }

        // Validate phone format (Vietnamese format)
        if (!phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            phoneEditText.setError("Invalid phone number format");
            phoneEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.password_mismatch));
            confirmPasswordEditText.requestFocus();
            return;
        }

        setLoading(true);

        authRepository.register(email, password, username, phone, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(RegisterActivity.this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                    // Go back to login
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Snackbar.make(registerButton, message, Snackbar.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        registerButton.setEnabled(!loading);
        registerButton.setText(loading ? R.string.loading : R.string.register);
    }
}
