package com.uit.gamestore.data.remote;

import com.uit.gamestore.data.remote.dto.LoginRequest;
import com.uit.gamestore.data.remote.dto.LoginResponse;
import com.uit.gamestore.data.remote.dto.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/customer/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("auth/customer/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
