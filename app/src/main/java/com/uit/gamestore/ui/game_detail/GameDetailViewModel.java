package com.uit.gamestore.ui.game_detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.remote.RetrofitClient;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.ReviewDto;
import com.uit.gamestore.data.repository.GameRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameDetailViewModel extends ViewModel {

    private final MutableLiveData<GameDto> game = new MutableLiveData<>();
    private final MutableLiveData<List<ReviewDto>> reviews = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final GameRepository gameRepository = new GameRepository();
    private String currentGameId;

    public void loadGame(@NonNull String gameId) {
        if (gameId.isEmpty()) {
            error.setValue("Invalid game ID");
            return;
        }

        currentGameId = gameId;
        isLoading.setValue(true);
        error.setValue(null);

        gameRepository.getGameById(gameId, new GameRepository.GameDetailCallback() {
            @Override
            public void onSuccess(GameDto gameDto) {
                game.postValue(gameDto);
                isLoading.postValue(false);
                loadReviews(gameId);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private void loadReviews(@NonNull String gameId) {
        RetrofitClient.getGameApi().getGameReviews(gameId, "date_desc")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ReviewDto>> call,
                                           @NonNull Response<List<ReviewDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            reviews.postValue(response.body());
                        } else {
                            reviews.postValue(Collections.emptyList());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ReviewDto>> call, @NonNull Throwable t) {
                        reviews.postValue(Collections.emptyList());
                    }
                });
    }

    public void refresh() {
        if (currentGameId != null && !currentGameId.isEmpty()) {
            loadGame(currentGameId);
        }
    }

    public void clearError() {
        error.setValue(null);
    }

    public LiveData<GameDto> getGame() {
        return game;
    }

    public LiveData<List<ReviewDto>> getReviews() {
        return reviews;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
}
