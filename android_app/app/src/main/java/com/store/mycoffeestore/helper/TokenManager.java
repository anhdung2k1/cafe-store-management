package com.store.mycoffeestore.helper;

import android.content.Context;
import android.content.SharedPreferences;

// Helper class to store and retrieve JWT token and username
public class TokenManager {

    private static final String PREF_NAME = "my_app_prefs";
    private static final String TOKEN_KEY = "auth_token";
    private static final String USERNAME_KEY = "user_name";

    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save JWT token
    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply();
    }

    // Get JWT token
    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    // Save username
    public void saveUserName(String userName) {
        sharedPreferences.edit().putString(USERNAME_KEY, userName).apply();
    }

    // Get username
    public String getUserName() {
        return sharedPreferences.getString(USERNAME_KEY, null);
    }

    // Clear token and username (logout)
    public void clearSession() {
        sharedPreferences.edit()
                .remove(TOKEN_KEY)
                .remove(USERNAME_KEY)
                .apply();
    }
}