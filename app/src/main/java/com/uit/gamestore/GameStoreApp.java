package com.uit.gamestore;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.uit.gamestore.data.local.TokenManager;

public class GameStoreApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        TokenManager.init(this);
    }
}
