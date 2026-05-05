package com.example.appmusica.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PerfilService {

    @Headers("Content-Type: application/json")
    @POST("perfiles")
    Call<Void> crearPerfil(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Header("Prefer") String prefer,
            @Body PerfilRequest perfil
    );
}