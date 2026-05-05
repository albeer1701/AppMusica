package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyTrack {

    public String id;
    public String name;

    @SerializedName("duration_ms")
    public int durationMs;

    public SpotifyAlbum album;
    public List<SpotifyArtist> artists;

    @SerializedName("external_urls")
    public SpotifyExternalUrls externalUrls;

    public String getMainArtistName() {
        if (artists == null || artists.isEmpty() || artists.get(0) == null) {
            return "";
        }

        return artists.get(0).name != null ? artists.get(0).name : "";
    }

    public String getAlbumName() {
        if (album == null || album.name == null) {
            return "";
        }

        return album.name;
    }

    public String getImageUrl() {
        if (album == null) {
            return "";
        }

        return album.getImageUrl();
    }

    public int getYear() {
        if (album == null) {
            return 0;
        }

        return album.getYear();
    }

    public String getDurationText() {
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}