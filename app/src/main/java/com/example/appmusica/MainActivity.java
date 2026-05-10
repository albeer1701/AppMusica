package com.example.appmusica;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmusica.spotify.SpotifyApiService;
import com.example.appmusica.spotify.SpotifyArtist;
import com.example.appmusica.spotify.SpotifyAuthService;
import com.example.appmusica.spotify.SpotifySearchResponse;
import com.example.appmusica.spotify.SpotifyTokenResponse;
import com.example.appmusica.spotify.SpotifyTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageButton navHome;
    private Button navSongs;
    private Button navAlbums;
    private Button navProfile;
    private Button btnExploreMusic;
    private Button btnMyLibrary;

    private LinearLayout sectionHome;
    private LinearLayout sectionSongs;
    private LinearLayout sectionAlbums;
    private LinearLayout sectionProfile;

    private EditText etSearchSongs;
    private EditText etSearchAlbums;

    private RecyclerView rvSongs;
    private RecyclerView rvAlbums;
    private RecyclerView rvHomeRecent;

    private SpotifyApiService spotifyApiService;
    private SpotifyAuthService spotifyAuthService;

    private String spotifyAccessToken = "";

    private SpotifySongAdapter songAdapter;
    private final List<SpotifyTrack> songList = new ArrayList<>();

    private static final String CLIENT_ID = "bc402c839df64040aca87380ef67c5a8";
    private static final String CLIENT_SECRET = "eb6414a78146405aa9c05384567839cd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHome = findViewById(R.id.navHome);
        navSongs = findViewById(R.id.navSongs);
        navAlbums = findViewById(R.id.navAlbums);
        navProfile = findViewById(R.id.navProfile);

        btnExploreMusic = findViewById(R.id.btnExploreMusic);
        btnMyLibrary = findViewById(R.id.btnMyLibrary);

        sectionHome = findViewById(R.id.sectionHome);
        sectionSongs = findViewById(R.id.sectionSongs);
        sectionAlbums = findViewById(R.id.sectionAlbums);
        sectionProfile = findViewById(R.id.sectionProfile);

        etSearchSongs = findViewById(R.id.etSearchSongs);
        etSearchAlbums = findViewById(R.id.etSearchAlbums);

        rvSongs = findViewById(R.id.rvSongs);
        rvAlbums = findViewById(R.id.rvAlbums);
        rvHomeRecent = findViewById(R.id.rvHomeRecent);

        setupRecyclerViews();
        setupSpotifyRetrofit();
        getSpotifyToken();
        setupNavigation();
        setupSearch();
    }

    private void setupRecyclerViews() {
        rvSongs.setLayoutManager(new LinearLayoutManager(this));

        songAdapter = new SpotifySongAdapter(songList, song -> openSongDetail(song));

        rvSongs.setAdapter(songAdapter);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> showSection(sectionHome));

        navSongs.setOnClickListener(v -> {
            showSection(sectionSongs);

            if (spotifyAccessToken == null || spotifyAccessToken.isEmpty()) {
                Toast.makeText(this, "Token de Spotify todavía no cargado", Toast.LENGTH_SHORT).show();
            } else {
                searchSpotifySongs("pop");
            }
        });

        navAlbums.setOnClickListener(v -> showSection(sectionAlbums));

        navProfile.setOnClickListener(v -> showSection(sectionProfile));

        btnExploreMusic.setOnClickListener(v -> {
            showSection(sectionSongs);

            if (spotifyAccessToken == null || spotifyAccessToken.isEmpty()) {
                Toast.makeText(this, "Token de Spotify todavía no cargado", Toast.LENGTH_SHORT).show();
            } else {
                searchSpotifySongs("rock");
            }
        });

        btnMyLibrary.setOnClickListener(v ->
                Toast.makeText(this, "Mi Biblioteca", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupSearch() {
        etSearchSongs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.length() >= 2 && spotifyAccessToken != null && !spotifyAccessToken.isEmpty()) {
                    searchSpotifySongs(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showSection(LinearLayout sectionToShow) {
        sectionHome.setVisibility(View.GONE);
        sectionSongs.setVisibility(View.GONE);
        sectionAlbums.setVisibility(View.GONE);
        sectionProfile.setVisibility(View.GONE);

        sectionToShow.setVisibility(View.VISIBLE);
    }

    private void setupSpotifyRetrofit() {
        Retrofit authRetrofit = new Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spotifyAuthService = authRetrofit.create(SpotifyAuthService.class);

        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spotifyApiService = apiRetrofit.create(SpotifyApiService.class);
    }

    private void getSpotifyToken() {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;

        String basicAuth = "Basic " + Base64.encodeToString(
                credentials.getBytes(),
                Base64.NO_WRAP
        );

        spotifyAuthService.getToken(
                basicAuth,
                "client_credentials"
        ).enqueue(new Callback<SpotifyTokenResponse>() {
            @Override
            public void onResponse(Call<SpotifyTokenResponse> call, Response<SpotifyTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    spotifyAccessToken = response.body().getAccessToken();

                    Toast.makeText(
                            MainActivity.this,
                            "Spotify conectado",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {
                    String errorText = "Sin detalle";

                    try {
                        if (response.errorBody() != null) {
                            errorText = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorText = e.getMessage();
                    }

                    Toast.makeText(
                            MainActivity.this,
                            "Error token Spotify " + response.code() + ": " + errorText,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<SpotifyTokenResponse> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Fallo token Spotify: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void searchSpotifySongs(String query) {
        query = query.trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "La búsqueda está vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        spotifyApiService.searchTracks(
                "Bearer " + spotifyAccessToken,
                query,
                "track",
                10,
                "ES"
        ).enqueue(new Callback<SpotifySearchResponse>() {
            @Override
            public void onResponse(Call<SpotifySearchResponse> call, Response<SpotifySearchResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getTracks() != null
                        && response.body().getTracks().getItems() != null) {

                    songList.clear();
                    songList.addAll(response.body().getTracks().getItems());
                    songAdapter.notifyDataSetChanged();

                    Toast.makeText(
                            MainActivity.this,
                            "Canciones cargadas: " + songList.size(),
                            Toast.LENGTH_SHORT
                    ).show();

                } else {
                    String errorText = "Sin detalle";

                    try {
                        if (response.errorBody() != null) {
                            errorText = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorText = e.getMessage();
                    }

                    Toast.makeText(
                            MainActivity.this,
                            "Error Spotify " + response.code() + ": " + errorText,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<SpotifySearchResponse> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Fallo búsqueda Spotify: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void openSongDetail(SpotifyTrack song) {
        String artistName = "Artista desconocido";

        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            SpotifyArtist artist = song.getArtists().get(0);

            if (artist != null && artist.getName() != null) {
                artistName = artist.getName();
            }
        }

        Intent intent = new Intent(MainActivity.this, SongDetail.class);
        intent.putExtra("song_id", song.getId());
        intent.putExtra("song_name", song.getName());
        intent.putExtra("artist_name", artistName);

        startActivity(intent);
    }

    private static class SpotifySongAdapter extends RecyclerView.Adapter<SpotifySongAdapter.SongViewHolder> {

        private final List<SpotifyTrack> songs;
        private final OnSongClickListener listener;

        public SpotifySongAdapter(List<SpotifyTrack> songs, OnSongClickListener listener) {
            this.songs = songs;
            this.listener = listener;
        }

        @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(24, 20, 24, 20);
            layout.setBackgroundColor(0x223E6A8F);

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 16);
            layout.setLayoutParams(params);

            TextView tvTitle = new TextView(parent.getContext());
            tvTitle.setTextColor(0xFFFFFFFF);
            tvTitle.setTextSize(17);
            tvTitle.setTypeface(null, Typeface.BOLD);

            TextView tvArtist = new TextView(parent.getContext());
            tvArtist.setTextColor(0xFFA7B6C2);
            tvArtist.setTextSize(14);

            layout.addView(tvTitle);
            layout.addView(tvArtist);

            return new SongViewHolder(layout, tvTitle, tvArtist);
        }

        @Override
        public void onBindViewHolder(SongViewHolder holder, int position) {
            SpotifyTrack song = songs.get(position);

            holder.tvTitle.setText(song.getName());

            String artistName = "Artista desconocido";

            if (song.getArtists() != null && !song.getArtists().isEmpty()) {
                SpotifyArtist artist = song.getArtists().get(0);

                if (artist != null && artist.getName() != null) {
                    artistName = artist.getName();
                }
            }

            holder.tvArtist.setText(artistName);

            holder.itemView.setOnClickListener(v -> listener.onSongClick(song));
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        static class SongViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;
            TextView tvArtist;

            public SongViewHolder(View itemView, TextView tvTitle, TextView tvArtist) {
                super(itemView);
                this.tvTitle = tvTitle;
                this.tvArtist = tvArtist;
            }
        }

        interface OnSongClickListener {
            void onSongClick(SpotifyTrack song);
        }
    }
}