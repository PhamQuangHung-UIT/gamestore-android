package com.uit.gamestore.ui.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.repository.GameRepository;
import com.uit.gamestore.domain.model.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StoreViewModel extends ViewModel {

    private final MutableLiveData<List<Game>> gameLists = new MutableLiveData<>();

    private final MutableLiveData<List<CategorySection>> categorySections = new MutableLiveData<>(new ArrayList<>());

    private final GameRepository m_gameRepository = new GameRepository();



    public StoreViewModel() {
    }


    public void getAllGames() {
        gameLists.setValue(m_gameRepository.getAllGames());
    }

    public void getGamesByCategory(String category) {
        var section = new CategorySection(category, m_gameRepository.getGamesByCategory(category));
        var newList = new ArrayList<>(Objects.requireNonNull(categorySections.getValue()));
        newList.add(section);
        categorySections.setValue(newList);
    }

    public LiveData<List<Game>> getGameLists() {
        return gameLists;
    }

    public LiveData<List<CategorySection>> getCategorySections() {
        return categorySections;
    }
}