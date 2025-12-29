package com.uit.gamestore.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    // Change this to your actual server URL
    // For Android emulator accessing localhost: use 10.0.2.2
    // For physical device: use your computer's IP address
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static volatile Retrofit publicRetrofit;
    private static volatile Retrofit authenticatedRetrofit;
    private static volatile OkHttpClient publicClient;
    private static volatile OkHttpClient authenticatedClient;

    private RetrofitClient() {}

    private static HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    private static OkHttpClient getPublicClient() {
        if (publicClient == null) {
            synchronized (RetrofitClient.class) {
                if (publicClient == null) {
                    publicClient = new OkHttpClient.Builder()
                            .addInterceptor(createLoggingInterceptor())
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return publicClient;
    }

    private static OkHttpClient getAuthenticatedClient() {
        if (authenticatedClient == null) {
            synchronized (RetrofitClient.class) {
                if (authenticatedClient == null) {
                    authenticatedClient = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor())
                            .addInterceptor(createLoggingInterceptor())
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return authenticatedClient;
    }

    private static Gson createGson() {
        return new GsonBuilder().setLenient().create();
    }

    /**
     * Public Retrofit instance - NO authentication header
     * Use for: games list, game details, reviews (read-only)
     */
    public static Retrofit getPublicInstance() {
        if (publicRetrofit == null) {
            synchronized (RetrofitClient.class) {
                if (publicRetrofit == null) {
                    publicRetrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(getPublicClient())
                            .addConverterFactory(GsonConverterFactory.create(createGson()))
                            .build();
                }
            }
        }
        return publicRetrofit;
    }

    /**
     * Authenticated Retrofit instance - includes JWT token
     * Use for: customer profile, orders, library, protected endpoints
     */
    public static Retrofit getAuthenticatedInstance() {
        if (authenticatedRetrofit == null) {
            synchronized (RetrofitClient.class) {
                if (authenticatedRetrofit == null) {
                    authenticatedRetrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(getAuthenticatedClient())
                            .addConverterFactory(GsonConverterFactory.create(createGson()))
                            .build();
                }
            }
        }
        return authenticatedRetrofit;
    }

    // ============ PUBLIC APIs (No login required) ============

    /**
     * Auth API - for login/register (public)
     */
    public static AuthApi getAuthApi() {
        return getPublicInstance().create(AuthApi.class);
    }

    /**
     * Game API - for browsing games (public)
     */
    public static GameApi getGameApi() {
        return getPublicInstance().create(GameApi.class);
    }

    // ============ PROTECTED APIs (Login required) ============

    /**
     * Customer API - for profile, orders, library (requires login)
     */
    public static CustomerApi getCustomerApi() {
        return getAuthenticatedInstance().create(CustomerApi.class);
    }
}
