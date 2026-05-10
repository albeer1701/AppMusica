package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyTrack {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("uri")
    private String uri;

    @SerializedName("artists")
    private List<SpotifyArtist> artists;

    @SerializedName("album")
    private SpotifyAlbum album;

    @SerializedName("external_urls")
    private SpotifyExternalUrls externalUrls;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public List<SpotifyArtist> getArtists() {
        return artists;
    }

    public SpotifyAlbum getAlbum() {
        return album;
    }

    public SpotifyExternalUrls getExternalUrls() {
        return externalUrls;
    }

    public String getImageUrl() {
        if (album == null) {
            return "";
        }

        return album.getImageUrl();
    }
}