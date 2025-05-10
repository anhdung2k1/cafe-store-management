package com.store.mycoffeestore.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.store.mycoffeestore.helper.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// Interceptor to attach Authorization header with Bearer token
public class TokenInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public TokenInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();

        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        // Add Authorization header only if token is available
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        Request modifiedRequest = builder.build();
        return chain.proceed(modifiedRequest);
    }
}
