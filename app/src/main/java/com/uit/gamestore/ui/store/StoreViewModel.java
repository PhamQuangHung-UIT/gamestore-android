package com.uit.gamestore.ui.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.repository.GameRepository;

import java.util.List;

public class StoreViewModel extends ViewModel {

    private final MutableLiveData<List<GameDto>> allGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameDto>> saleGames = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final GameRepository gameRepository = new GameRepository();

    public void loadAllGames() {
        isLoading.setValue(true);
        gameRepository.getAllGames(new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                allGames.postValue(games);
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
        gameRepository.searchGames(null, null, true, new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                saleGames.postValue(games);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        });
    }

    public void searchGames(String query) {
        isLoading.setValue(true);
        gameRepository.searchGames(query, null, null, new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                allGames.postValue(games);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
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