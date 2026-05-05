package com.example.appmusica.api;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("p_email")
    public String email;

    @SerializedName("p_password")
    public String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}