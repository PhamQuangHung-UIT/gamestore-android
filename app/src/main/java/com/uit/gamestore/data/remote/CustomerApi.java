package com.uit.gamestore.data.remote;

import com.uit.gamestore.data.remote.dto.CustomerProfileDto;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.OrderDto;
import com.uit.gamestore.data.remote.dto.OrderRequest;
import com.uit.gamestore.data.remote.dto.OrderResponse;
import com.uit.gamestore.data.remote.dto.WishlistResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Call<OrderResponse> createOrder(@Body OrderRequest request);

    @GET("customers/me/library")
    Call<List<GameDto>> getLibrary();

    @GET("customers/me/wishlist")
    Call<List<GameDto>> getWishlist();

    @POST("customers/me/wishlist/{gameId}")
    Call<WishlistResponse> addToWishlist(@Path("gameId") String gameId);

    @DELETE("customers/me/wishlist/{gameId}")
    Call<WishlistResponse> removeFromWishlist(@Path("gameId") String gameId);
}
