package com.uit.gamestore.data.repository;

import androidx.annotation.NonNull;

import com.uit.gamestore.data.local.TokenManager;
import com.uit.gamestore.data.remote.RetrofitClient;
import com.uit.gamestore.data.remote.dto.LoginRequest;
import com.uit.gamestore.data.remote.dto.LoginResponse;
import com.uit.gamestore.data.remote.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    public interface AuthCallback {
        void onSuccess(LoginResponse response);
        void onError(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getAuthApi().login(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    saveSession(loginResponse);
                    callback.onSuccess(loginResponse);
                } else {
                    callback.onError(parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(String email, String password, String username, String phoneNumber, AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(email, password, username, phoneNumber);

        RetrofitClient.getAuthApi().register(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Don't save session - user should login after registration
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void logout() {
        TokenManager.getInstance().clearSession();
    }

    public boolean isLoggedIn() {
        return TokenManager.getInstance().isLoggedIn();
    }

    private void saveSession(LoginResponse response) {
        if (response == null || response.getToken() == null) {
            return;
        }

        TokenManager tokenManager = TokenManager.getInstance();
        tokenManager.saveToken(response.getToken());

        LoginResponse.CustomerDto customer = response.getCustomer();
        if (customer != null) {
            tokenManager.saveUserInfo(
                    customer.getId() != null ? customer.getId() : "",
                    customer.getEmail() != null ? customer.getEmail() : "",
                    customer.getUsername() != null ? customer.getUsername() : ""
            );
        }
    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                if (errorBody != null && !errorBody.isEmpty()) {
                    return errorBody;
                }
            }
        } catch (Exception e) {
            // ignore parsing error
        }

        int code = response.code();
        if (code == 401) {
            return "Invalid email or password";
        } else if (code == 400) {
            return "Invalid request. Please check your input.";
        } else if (code >= 500) {
            return "Server error. Please try again later.";
        }

        return "Login failed. Please check your credentials.";
    }
}
