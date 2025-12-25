package com.uit.gamestore.ui.your_games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class YourGamesViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();

    public YourGamesViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}