package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyPlaylist {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("images")
    private List<SpotifyImage> images;

    @SerializedName("external_urls")
    private SpotifyExternalUrls externalUrls;

    public String getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : "Playlist";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getImageUrl() {
        if (images == null || images.isEmpty() || images.get(0) == null) {
            return "";
        }

        return images.get(0).getUrl();
    }

    public String getSpotifyUrl() {
        if (externalUrls == null || externalUrls.getSpotify() == null) {
            return "";
        }

        return externalUrls.getSpotify();
    }
}