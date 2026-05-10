package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyAlbum {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("artists")
    public List<SpotifyArtist> artists;

    @SerializedName("images")
    public List<SpotifyImage> images;

    @SerializedName("external_urls")
    public SpotifyExternalUrls externalUrls;

    public String getMainArtistName() {
        if (artists == null || artists.isEmpty() || artists.get(0) == null) {
            return "";
        }

        return artists.get(0).getName() != null ? artists.get(0).getName() : "";
    }

    public String getImageUrl() {
        if (images == null || images.isEmpty() || images.get(0) == null) {
            return "";
        }

        return images.get(0).getUrl() != null ? images.get(0).getUrl() : "";
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