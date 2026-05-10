package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyPlaylistTrackItem {

    @SerializedName("track")
    private SpotifyTrack track;

    public SpotifyTrack getTrack() {
        return track;
    }
}