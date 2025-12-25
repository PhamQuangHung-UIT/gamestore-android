package com.uit.gamestore;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class GameStoreApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
