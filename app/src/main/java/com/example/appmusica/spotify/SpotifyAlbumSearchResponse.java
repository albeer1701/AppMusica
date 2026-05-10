package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyAlbumSearchResponse {

    @SerializedName("albums")
    private SpotifyAlbumsWrapper albums;

    public SpotifyAlbumsWrapper getAlbums() {
        return albums;
    }
}