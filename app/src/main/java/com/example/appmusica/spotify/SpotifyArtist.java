package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyArtist {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}