package com.uit.gamestore.ui.save_games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavedGamesViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();

    public SavedGamesViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}