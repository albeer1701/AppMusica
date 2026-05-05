package com.example.appmusica.api;

import com.google.gson.annotations.SerializedName;

public class PerfilRequest {

    public String id;

    @SerializedName("nombre_usuario")
    public String nombreUsuario;

    @SerializedName("foto_perfil_url")
    public String fotoPerfilUrl;

    public String descripcion;
    public String rol;

    public PerfilRequest(String id, String nombreUsuario, String fotoPerfilUrl, String descripcion, String rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.fotoPerfilUrl = fotoPerfilUrl;
        this.descripcion = descripcion;
        this.rol = rol;
    }
}