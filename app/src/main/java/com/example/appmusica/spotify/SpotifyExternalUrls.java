package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyExternalUrls {

    @SerializedName("spotify")
    private String spotify;

    public String getSpotify() {
        return spotify;
    }
}