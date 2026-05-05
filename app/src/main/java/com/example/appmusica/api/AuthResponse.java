package com.example.appmusica.api;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("token_type")
    public String tokenType;

    public AuthUser user;
}