package com.example.appmusica.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SupabaseService {

    @GET("canciones?select=*")
    Call<List<SongRemote>> getCanciones(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @GET("albumes?select=*")
    Call<List<AlbumRemote>> getAlbumes(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );
}