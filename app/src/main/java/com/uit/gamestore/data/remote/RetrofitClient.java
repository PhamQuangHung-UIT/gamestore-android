package com.uit.gamestore.data.remote;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static Retrofit retrofit;

    private RetrofitClient() {}

    public static Retrofit getInstance() {
        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(
                            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
                    )
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.example.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static UserAPI getApiService() {
        return getInstance().create(UserAPI.class);
    }
}

