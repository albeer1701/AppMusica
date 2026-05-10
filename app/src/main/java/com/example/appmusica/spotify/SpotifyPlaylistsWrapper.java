package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyPlaylistsWrapper {

    @SerializedName("items")
    private List<SpotifyPlaylist> items;

    public List<SpotifyPlaylist> getItems() {
        return items;
    }
}