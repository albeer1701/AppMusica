package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyAlbum {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("popularity")
    private int popularity;

    @SerializedName("artists")
    private List<SpotifyArtist> artists;

    @SerializedName("images")
    private List<SpotifyImage> images;

    @SerializedName("external_urls")
    private SpotifyExternalUrls externalUrls;

    public String getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : "Álbum";
    }

    public String getReleaseDate() {
        return releaseDate != null ? releaseDate : "";
    }

    public int getPopularity() {
        return popularity;
    }

    public String getMainArtistName() {
        if (artists == null || artists.isEmpty() || artists.get(0) == null) {
            return "Artista desconocido";
        }

        String artistName = artists.get(0).getName();
        return artistName != null ? artistName : "Artista desconocido";
    }

    public String getImageUrl() {
        if (images == null || images.isEmpty() || images.get(0) == null) {
            return "";
        }

        String imageUrl = images.get(0).getUrl();
        return imageUrl != null ? imageUrl : "";
    }

    public String getSpotifyUrl() {
        if (externalUrls == null || externalUrls.getSpotify() == null) {
            return "";
        }

        return externalUrls.getSpotify();
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
