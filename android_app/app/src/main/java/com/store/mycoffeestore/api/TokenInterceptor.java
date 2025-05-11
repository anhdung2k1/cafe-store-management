package com.store.mycoffeestore.api;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.store.mycoffeestore.activity.IntroActivity;
import com.store.mycoffeestore.helper.TokenManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private final TokenManager tokenManager;
    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context;
        this.tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();

        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (token != null && !token.isEmpty()) {
            if (isTokenExpired(token)) {
                // Token expired → clear & redirect
                tokenManager.clearSession();

                Intent intent = new Intent(context, IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

                throw new IOException("Token expired. Redirecting to login.");
            }

            builder.header("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }

    private boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return true;

            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject json = new JSONObject(payload);
            long exp = json.getLong("exp");

            long now = System.currentTimeMillis() / 1000;
            return exp < now;
        } catch (Exception e) {
            Log.e("TokenInterceptor", "Failed to parse token expiration", e);
            return true;
        }
    }
}