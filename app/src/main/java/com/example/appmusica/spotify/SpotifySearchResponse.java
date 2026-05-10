package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifySearchResponse {

    @SerializedName("tracks")
    private SpotifyTracksWrapper tracks;

    public SpotifyTracksWrapper getTracks() {
        return tracks;
    }
}