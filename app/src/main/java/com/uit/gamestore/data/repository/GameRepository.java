package com.uit.gamestore.data.repository;

import androidx.annotation.NonNull;

import com.uit.gamestore.data.remote.RetrofitClient;
import com.uit.gamestore.data.remote.dto.GameDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRepository {

    public interface GamesCallback {
        void onSuccess(List<GameDto> games);
        void onError(String message);
    }

    public interface GameDetailCallback {
        void onSuccess(GameDto game);
        void onError(String message);
    }

    public void getAllGames(GamesCallback callback) {
        RetrofitClient.getGameApi().getAllGames().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<GameDto>> call, @NonNull Response<List<GameDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load games");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GameDto>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void searchGames(String search, String genre, Boolean onSale, GamesCallback callback) {
        RetrofitClient.getGameApi().getGames(search, genre, null, onSale).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<GameDto>> call, @NonNull Response<List<GameDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to search games");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GameDto>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getGameById(String id, GameDetailCallback callback) {
        RetrofitClient.getGameApi().getGameById(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GameDto> call, @NonNull Response<GameDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load game details");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GameDto> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
