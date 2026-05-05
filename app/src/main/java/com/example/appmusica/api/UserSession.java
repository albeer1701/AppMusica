package com.example.appmusica.api;

import com.google.gson.annotations.SerializedName;

public class UserSession {

    public String id;
    public String email;

    @SerializedName("nombre_usuario")
    public String nombreUsuario;

    public String rol;
}