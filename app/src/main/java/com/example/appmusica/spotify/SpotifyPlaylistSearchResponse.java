package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyPlaylistSearchResponse {

    @SerializedName("playlists")
    private SpotifyPlaylistsWrapper playlists;

    public SpotifyPlaylistsWrapper getPlaylists() {
        return playlists;
    }
}