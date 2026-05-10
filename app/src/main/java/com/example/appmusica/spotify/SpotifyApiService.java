package com.example.appmusica.spotify;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpotifyApiService {

    @GET("search")
    Call<SpotifySearchResponse> searchTracks(
            @Header("Authorization") String authorization,
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit,
            @Query("market") String market
    );

    @GET("search")
    Call<SpotifyPlaylistSearchResponse> searchPlaylists(
            @Header("Authorization") String authorization,
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit,
            @Query("market") String market
    );

    @GET("search")
    Call<SpotifyAlbumSearchResponse> searchAlbums(
            @Header("Authorization") String authorization,
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit,
            @Query("market") String market
    );

    @GET("albums/{album_id}/tracks")
    Call<SpotifyAlbumTracksResponse> getAlbumTracks(
            @Header("Authorization") String authorization,
            @Path("album_id") String albumId,
            @Query("limit") int limit,
            @Query("market") String market
    );
}