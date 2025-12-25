package com.uit.gamestore.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();

    public SettingsViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}