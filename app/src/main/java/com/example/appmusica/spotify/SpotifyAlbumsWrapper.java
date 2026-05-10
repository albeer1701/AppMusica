package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyAlbumsWrapper {

    @SerializedName("items")
    private List<SpotifyAlbum> items;

    public List<SpotifyAlbum> getItems() {
        return items;
    }
}