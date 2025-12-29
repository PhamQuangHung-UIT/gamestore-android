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

    private static volatile Retrofit retrofit;
    private static volatile OkHttpClient okHttpClient;

    private RetrofitClient() {}

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (RetrofitClient.class) {
                if (okHttpClient == null) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor())
                            .addInterceptor(loggingInterceptor)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(getOkHttpClient())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static AuthApi getAuthApi() {
        return getInstance().create(AuthApi.class);
    }

    public static GameApi getGameApi() {
        return getInstance().create(GameApi.class);
    }

    public static CustomerApi getCustomerApi() {
        return getInstance().create(CustomerApi.class);
    }
}
