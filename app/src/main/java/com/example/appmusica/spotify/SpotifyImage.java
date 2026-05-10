package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyImage {

    @SerializedName("url")
    private String url;

    @SerializedName("height")
    private int height;

    @SerializedName("width")
    private int width;

    public String getUrl() {
        return url;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}