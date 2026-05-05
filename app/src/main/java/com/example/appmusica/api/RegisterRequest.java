package com.example.appmusica.api;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest {

    public String email;
    public String password;
    public Map<String, String> data;

    public RegisterRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.data = new HashMap<>();
        this.data.put("username", username);
    }
}