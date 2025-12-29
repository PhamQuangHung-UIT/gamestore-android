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

    public void register(String email, String password, String username, AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(email, password, username);

        RetrofitClient.getAuthApi().register(request).enqueue(new Callback<>() {
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

    public void logout() {
        TokenManager.getInstance().clearSession();
    }

    public boolean isLoggedIn() {
        return TokenManager.getInstance().isLoggedIn();
    }

    private void saveSession(LoginResponse response) {
        TokenManager tokenManager = TokenManager.getInstance();
        tokenManager.saveToken(response.getToken());
        if (response.getCustomer() != null) {
            tokenManager.saveUserInfo(
                    response.getCustomer().getId(),
                    response.getCustomer().getEmail(),
                    response.getCustomer().getUsername()
            );
        }
    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (Exception e) {
            // ignore
        }
        return "Login failed. Please check your credentials.";
    }
}
