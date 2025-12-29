package com.uit.gamestore.data.repository;

import androidx.annotation.NonNull;

import com.uit.gamestore.data.remote.RetrofitClient;
import com.uit.gamestore.data.remote.dto.CustomerProfileDto;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.OrderDto;
import com.uit.gamestore.data.remote.dto.OrderRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {

    public interface ProfileCallback {
        void onSuccess(CustomerProfileDto profile);
        void onError(String message);
    }

    public interface LibraryCallback {
        void onSuccess(List<GameDto> games);
        void onError(String message);
    }

    public interface OrdersCallback {
        void onSuccess(List<OrderDto> orders);
        void onError(String message);
    }

    public interface OrderCallback {
        void onSuccess(OrderDto order);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public void getProfile(ProfileCallback callback) {
        RetrofitClient.getCustomerApi().getProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CustomerProfileDto> call, @NonNull Response<CustomerProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load profile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateProfile(Map<String, Object> updates, ProfileCallback callback) {
        RetrofitClient.getCustomerApi().updateProfile(updates).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CustomerProfileDto> call, @NonNull Response<CustomerProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update profile");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerProfileDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void changePassword(String currentPassword, String newPassword, SimpleCallback callback) {
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", currentPassword);
        body.put("newPassword", newPassword);

        RetrofitClient.getCustomerApi().changePassword(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to change password");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getLibrary(LibraryCallback callback) {
        RetrofitClient.getCustomerApi().getLibrary().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<GameDto>> call, @NonNull Response<List<GameDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load library");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GameDto>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getOrders(OrdersCallback callback) {
        RetrofitClient.getCustomerApi().getOrders().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDto>> call, @NonNull Response<List<OrderDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load orders");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDto>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createOrder(OrderRequest request, OrderCallback callback) {
        RetrofitClient.getCustomerApi().createOrder(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderDto> call, @NonNull Response<OrderDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create order");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
