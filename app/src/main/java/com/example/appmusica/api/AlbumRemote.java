package com.example.appmusica.api;

import com.google.gson.annotations.SerializedName;

public class AlbumRemote {

    public String id;

    @SerializedName("spotify_id")
    public String spotifyId;

    public String titulo;
    public String artista;
    public int anio;

    @SerializedName("numero_canciones")
    public int numeroCanciones;

    public String genero;

    @SerializedName("portada_url")
    public String portadaUrl;

    @SerializedName("nota_media")
    public double notaMedia;

    @SerializedName("numero_valoraciones")
    public String numeroValoraciones;

    @SerializedName("fecha_creacion")
    public String fechaCreacion;
}