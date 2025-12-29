package com.uit.gamestore.data.remote;

import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.PaginatedGamesResponse;
import com.uit.gamestore.data.remote.dto.ReviewDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GameApi {

    @GET("games/paginated")
    Call<PaginatedGamesResponse> getGamesPaginated(
            @Query("search") String search,
            @Query("genre") String genre,
            @Query("publisherId") String publisherId,
            @Query("onSale") Boolean onSale,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("games")
    Call<List<GameDto>> getGames(
            @Query("search") String search,
            @Query("genre") String genre,
            @Query("publisherId") String publisherId,
            @Query("releaseStatus") String releaseStatus,
            @Query("hasDiscount") Boolean hasDiscount
    );

    @GET("games")
    Call<List<GameDto>> getAllGames();

    @GET("games/{id}")
    Call<GameDto> getGameById(@Path("id") String id);

    @GET("games/{id}/reviews")
    Call<List<ReviewDto>> getGameReviews(
            @Path("id") String id,
            @Query("sort") String sort
    );
}
