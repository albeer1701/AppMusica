package com.example.appmusica.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {

    @Headers("Content-Type: application/json")
    @POST("rpc/iniciar_sesion_usuario")
    Call<List<UserSession>> login(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Body LoginRequest request
    );

    @Headers("Content-Type: application/json")
    @POST("rpc/registrar_usuario")
    Call<List<UserSession>> register(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization,
            @Body RegisterRequest request
    );
}