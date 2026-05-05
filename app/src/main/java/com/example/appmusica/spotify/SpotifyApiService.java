package com.example.appmusica.spotify;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SpotifyApiService {

    @GET("search")
    Call<SpotifySearchResponse> searchMusic(
            @Header("Authorization") String authorization,
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit,
            @Query("market") String market
    );
}