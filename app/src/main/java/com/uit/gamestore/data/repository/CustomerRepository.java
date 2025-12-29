package com.uit.gamestore.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uit.gamestore.data.remote.RetrofitClient;
import com.uit.gamestore.data.remote.dto.CustomerProfileDto;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.OrderDto;
import com.uit.gamestore.data.remote.dto.OrderRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {

    public interface ProfileCallback {
        void onSuccess(@NonNull CustomerProfileDto profile);
        void onError(@NonNull String message);
    }

    public interface LibraryCallback {
        void onSuccess(@NonNull List<GameDto> games);
        void onError(@NonNull String message);
    }

    public interface OrdersCallback {
        void onSuccess(@NonNull List<OrderDto> orders);
        void onError(@NonNull String message);
    }

    public interface OrderCallback {
        void onSuccess(@NonNull OrderDto order);
        void onError(@NonNull String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(@NonNull String message);
    }

    public void getProfile(@NonNull ProfileCallback callback) {
        RetrofitClient.getCustomerApi().getProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CustomerProfileDto> call, @NonNull Response<CustomerProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to load profile"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public void updateProfile(@Nullable Map<String, Object> updates, @NonNull ProfileCallback callback) {
        if (updates == null || updates.isEmpty()) {
            callback.onError("No updates provided");
            return;
        }

        RetrofitClient.getCustomerApi().updateProfile(updates).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CustomerProfileDto> call, @NonNull Response<CustomerProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to update profile"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public void changePassword(@Nullable String currentPassword, @Nullable String newPassword, @NonNull SimpleCallback callback) {
        if (currentPassword == null || currentPassword.isEmpty()) {
            callback.onError("Current password is required");
            return;
        }
        if (newPassword == null || newPassword.isEmpty()) {
            callback.onError("New password is required");
            return;
        }
        if (newPassword.length() < 8) {
            callback.onError("New password must be at least 8 characters");
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", currentPassword);
        body.put("newPassword", newPassword);

        RetrofitClient.getCustomerApi().changePassword(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(parseError(response, "Failed to change password"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public void getLibrary(@NonNull LibraryCallback callback) {
        RetrofitClient.getCustomerApi().getLibrary().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<GameDto>> call, @NonNull Response<List<GameDto>> response) {
                if (response.isSuccessful()) {
                    List<GameDto> games = response.body();
                    callback.onSuccess(games != null ? games : Collections.emptyList());
                } else {
                    callback.onError(parseError(response, "Failed to load library"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GameDto>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public void getOrders(@NonNull OrdersCallback callback) {
        RetrofitClient.getCustomerApi().getOrders().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDto>> call, @NonNull Response<List<OrderDto>> response) {
                if (response.isSuccessful()) {
                    List<OrderDto> orders = response.body();
                    callback.onSuccess(orders != null ? orders : Collections.emptyList());
                } else {
                    callback.onError(parseError(response, "Failed to load orders"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDto>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public void createOrder(@Nullable OrderRequest request, @NonNull OrderCallback callback) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            callback.onError("Order must contain at least one item");
            return;
        }

        RetrofitClient.getCustomerApi().createOrder(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderDto> call, @NonNull Response<OrderDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to create order"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    @NonNull
    private String parseError(@NonNull Response<?> response, @NonNull String defaultMessage) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                if (errorBody != null && !errorBody.isEmpty()) {
                    return errorBody;
                }
            }
        } catch (Exception e) {
            // ignore
        }

        int code = response.code();
        if (code == 401) {
            return "Please login again";
        } else if (code == 403) {
            return "You don't have permission to perform this action";
        } else if (code == 404) {
            return "Resource not found";
        } else if (code >= 500) {
            return "Server error. Please try again later.";
        }

        return defaultMessage;
    }
}
