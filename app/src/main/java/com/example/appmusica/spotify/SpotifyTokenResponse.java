package com.example.appmusica.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyTokenResponse {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_in")
    public int expiresIn;
}