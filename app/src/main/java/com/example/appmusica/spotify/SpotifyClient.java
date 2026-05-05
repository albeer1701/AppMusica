package com.example.appmusica.spotify;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyClient {

    private static SpotifyAuthService authService;
    private static SpotifyApiService apiService;

    public static SpotifyAuthService getAuthService() {
        if (authService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SpotifyConfig.AUTH_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            authService = retrofit.create(SpotifyAuthService.class);
        }

        return authService;
    }

    public static SpotifyApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SpotifyConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(SpotifyApiService.class);
        }

        return apiService;
    }

    public static String getBasicAuthHeader() {
        String credentials = SpotifyConfig.CLIENT_ID + ":" + SpotifyConfig.CLIENT_SECRET;
        String encoded = Base64.encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP
        );

        return "Basic " + encoded;
    }

    public static String getBearerHeader(String accessToken) {
        return "Bearer " + accessToken;
    }
}