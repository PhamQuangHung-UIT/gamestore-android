package com.uit.gamestore.data.repository;

import androidx.annotation.NonNull;

import com.uit.gamestore.data.remote.RetrofitClient;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.PaginatedGamesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRepository {

    public interface GamesCallback {
        void onSuccess(List<GameDto> games);
        void onError(String message);
    }

    public interface PaginatedGamesCallback {
        void onSuccess(List<GameDto> games, int total, int page, int totalPages);
        void onError(String message);
    }

    public interface GameDetailCallback {
        void onSuccess(GameDto game);
        void onError(String message);
    }

    public void getGamesPaginated(String search, String genre, Boolean onSale, int page, int limit, PaginatedGamesCallback callback) {
        RetrofitClient.getGameApi().getGamesPaginated(search, genre, null, onSale, page, limit).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedGamesResponse> call, @NonNull Response<PaginatedGamesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaginatedGamesResponse body = response.body();
                    callback.onSuccess(
                            body.getData(),
                            body.getMeta().getTotal(),
                            body.getMeta().getPage(),
                            body.getMeta().getTotalPages()
                    );
                } else {
                    callback.onError("Failed to load games");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedGamesResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getAllGames(GamesCallback callback) {
        getGamesPaginated(null, null, null, 1, 50, new PaginatedGamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games, int total, int page, int totalPages) {
                callback.onSuccess(games);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void searchGames(String search, String genre, Boolean onSale, GamesCallback callback) {
        getGamesPaginated(search, genre, onSale, 1, 50, new PaginatedGamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games, int total, int page, int totalPages) {
                callback.onSuccess(games);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void getGamesByGenre(String genre, GamesCallback callback) {
        getGamesPaginated(null, genre, null, 1, 50, new PaginatedGamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games, int total, int page, int totalPages) {
                callback.onSuccess(games);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void getDiscountedGames(GamesCallback callback) {
        getGamesPaginated(null, null, true, 1, 50, new PaginatedGamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games, int total, int page, int totalPages) {
                callback.onSuccess(games);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
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
