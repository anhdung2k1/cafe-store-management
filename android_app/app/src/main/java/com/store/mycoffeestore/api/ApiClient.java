package com.store.mycoffeestore.api;

import android.content.Context;

import com.store.mycoffeestore.helper.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit authRetrofit;   // no token
    private static Retrofit securedRetrofit; // with token

    /**
     * For public APIs like signIn, signUp — no token attached.
     */
    public static Retrofit getAuthRetrofit() {
        if (authRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            authRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return authRetrofit;
    }

    /**
     * For secured APIs — token will be automatically attached.
     */
    public static Retrofit getSecuredRetrofit(Context context) {
        if (securedRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(context))
                    .addInterceptor(logging)
                    .build();

            securedRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return securedRetrofit;
    }

    // Auth API (sign in, sign up)
    public static ApiService getAuthApiService() {
        return getAuthRetrofit().create(ApiService.class);
    }

    // Secured API (require token)
    public static ApiService getSecuredApiService(Context context) {
        return getSecuredRetrofit(context).create(ApiService.class);
    }
}