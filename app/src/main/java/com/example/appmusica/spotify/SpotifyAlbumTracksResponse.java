package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyAlbumTracksResponse {

    @SerializedName("items")
    private List<SpotifyTrack> items;

    public List<SpotifyTrack> getItems() {
        return items;
    }
}