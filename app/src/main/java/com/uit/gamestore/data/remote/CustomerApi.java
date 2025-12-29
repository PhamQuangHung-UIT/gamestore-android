package com.uit.gamestore.data.remote;

import com.uit.gamestore.data.remote.dto.CustomerProfileDto;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.OrderDto;
import com.uit.gamestore.data.remote.dto.OrderRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface CustomerApi {

    @GET("customers/me")
    Call<CustomerProfileDto> getProfile();

    @PATCH("customers/me")
    Call<CustomerProfileDto> updateProfile(@Body Map<String, Object> updates);

    @POST("customers/me/change-password")
    Call<Void> changePassword(@Body Map<String, String> passwords);

    @GET("customers/me/orders")
    Call<List<OrderDto>> getOrders();

    @POST("customers/me/orders")
    Call<OrderDto> createOrder(@Body OrderRequest request);

    @GET("customers/me/library")
    Call<List<GameDto>> getLibrary();
}
