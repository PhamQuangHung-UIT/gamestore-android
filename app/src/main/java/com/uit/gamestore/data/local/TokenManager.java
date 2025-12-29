package com.uit.gamestore.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class TokenManager {

    private static final String PREF_NAME = "game_store_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenManager not initialized. Call init() first.");
        }
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    @Nullable
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveUserInfo(String userId, String email, String name) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, name)
                .apply();
    }

    @Nullable
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    @Nullable
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    @Nullable
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
