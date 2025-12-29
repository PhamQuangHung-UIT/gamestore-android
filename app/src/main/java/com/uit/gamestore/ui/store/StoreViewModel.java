package com.uit.gamestore.ui.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.repository.GameRepository;

import java.util.Collections;
import java.util.List;

public class StoreViewModel extends ViewModel {

    private final MutableLiveData<List<GameDto>> allGames = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<GameDto>> saleGames = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final GameRepository gameRepository = new GameRepository();

    public void loadAllGames() {
        isLoading.setValue(true);
        error.setValue(null);

        gameRepository.getAllGames(new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                allGames.postValue(games != null ? games : Collections.emptyList());
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void loadSaleGames() {
        gameRepository.getDiscountedGames(new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                saleGames.postValue(games != null ? games : Collections.emptyList());
            }

            @Override
            public void onError(String message) {
                // Don't show error for sale games, just leave empty
            }
        });
    }

    public void searchGames(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadAllGames();
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        gameRepository.searchGames(query.trim(), null, null, new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                allGames.postValue(games != null ? games : Collections.emptyList());
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void clearError() {
        error.setValue(null);
    }

    public LiveData<List<GameDto>> getAllGames() {
        return allGames;
    }

    public LiveData<List<GameDto>> getSaleGames() {
        return saleGames;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
}