package com.store.mycoffeestore.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

// Helper class to store and retrieve JWT token and username
public class TokenManager {

    private static final String PREF_NAME = "my_app_prefs";
    private static final String TOKEN_KEY = "auth_token";

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
    // Clear token and username (logout)
    public void clearSession() {
        sharedPreferences.edit()
                .remove(TOKEN_KEY)
                .apply();
    }

    public String getUserNameFromToken() {
        try {
            String token = getToken();
            if (token == null || token.isEmpty()) return null;

            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject json = new JSONObject(payload);
            return json.optString("sub", null);
        } catch (Exception e) {
            Log.e("TokenManager", "Failed to extract sub from token", e);
            return null;
        }
    }
}