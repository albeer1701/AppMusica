package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyPlaylistTracksResponse {

    @SerializedName("items")
    private List<SpotifyPlaylistTrackItem> items;

    public List<SpotifyPlaylistTrackItem> getItems() {
        return items;
    }
}