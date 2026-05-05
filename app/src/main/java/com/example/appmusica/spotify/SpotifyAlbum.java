package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyAlbum {

    public String id;
    public String name;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("total_tracks")
    public int totalTracks;

    public List<SpotifyArtist> artists;
    public List<SpotifyImage> images;

    @SerializedName("external_urls")
    public SpotifyExternalUrls externalUrls;

    public String getMainArtistName() {
        if (artists == null || artists.isEmpty() || artists.get(0) == null) {
            return "";
        }

        return artists.get(0).name != null ? artists.get(0).name : "";
    }

    public String getImageUrl() {
        if (images == null || images.isEmpty() || images.get(0) == null) {
            return "";
        }

        return images.get(0).url != null ? images.get(0).url : "";
    }

    public int getYear() {
        if (releaseDate == null || releaseDate.length() < 4) {
            return 0;
        }

        try {
            return Integer.parseInt(releaseDate.substring(0, 4));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}