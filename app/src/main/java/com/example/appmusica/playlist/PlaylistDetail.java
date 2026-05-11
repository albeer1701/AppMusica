package com.example.appmusica.playlist;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusica.R;
import com.example.appmusica.song.SongDetail;
import com.example.appmusica.spotify.SpotifyApiService;
import com.example.appmusica.spotify.SpotifyArtist;
import com.example.appmusica.spotify.SpotifySearchResponse;
import com.example.appmusica.spotify.SpotifyTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistDetail extends AppCompatActivity {

    private Button btnBackPlaylist;
    private ImageView ivPlaylistCover;
    private TextView tvPlaylistTitle;
    private RecyclerView rvPlaylistSongs;

    private SpotifyApiService spotifyApiService;

    private PlaylistSongsAdapter adapter;
    private final List<SpotifyTrack> playlistSongs = new ArrayList<>();

    private String playlistName;
    private String playlistImageUrl;
    private String spotifyAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        btnBackPlaylist = findViewById(R.id.btnBackPlaylist);
        ivPlaylistCover = findViewById(R.id.ivPlaylistCover);
        tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle);
        rvPlaylistSongs = findViewById(R.id.rvPlaylistSongs);

        playlistName = getIntent().getStringExtra("playlist_name");
        playlistImageUrl = getIntent().getStringExtra("playlist_image_url");
        spotifyAccessToken = getIntent().getStringExtra("spotify_access_token");

        if (playlistName == null || playlistName.isEmpty()) {
            playlistName = "Top hits";
        }

        tvPlaylistTitle.setText(playlistName);

        if (playlistImageUrl != null && !playlistImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(playlistImageUrl)
                    .fitCenter()
                    .into(ivPlaylistCover);
        } else {
            ivPlaylistCover.setImageResource(R.drawable.logo);
        }

        btnBackPlaylist.setOnClickListener(v -> finish());

        setupRetrofit();
        setupRecyclerView();

        if (spotifyAccessToken == null || spotifyAccessToken.isEmpty()) {
            Toast.makeText(this, "Token de Spotify no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        loadSongsFromPlaylistName();
    }

    private void setupRetrofit() {
        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spotifyApiService = apiRetrofit.create(SpotifyApiService.class);
    }

    private void setupRecyclerView() {
        rvPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistSongsAdapter(playlistSongs, song -> openSongDetail(song));
        rvPlaylistSongs.setAdapter(adapter);
    }

    private void loadSongsFromPlaylistName() {
        spotifyApiService.searchTracks(
                "Bearer " + spotifyAccessToken,
                playlistName,
                "track",
                10,
                "ES"
        ).enqueue(new Callback<SpotifySearchResponse>() {
            @Override
            public void onResponse(Call<SpotifySearchResponse> call, Response<SpotifySearchResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getTracks() != null
                        && response.body().getTracks().getItems() != null) {

                    playlistSongs.clear();
                    playlistSongs.addAll(response.body().getTracks().getItems());
                    adapter.notifyDataSetChanged();

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
                            PlaylistDetail.this,
                            "Error canciones Spotify " + response.code() + ": " + errorText,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<SpotifySearchResponse> call, Throwable t) {
                Toast.makeText(
                        PlaylistDetail.this,
                        "Fallo canciones Spotify: " + t.getMessage(),
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

        Intent intent = new Intent(PlaylistDetail.this, SongDetail.class);
        intent.putExtra("song_id", song.getId());
        intent.putExtra("song_name", song.getName());
        intent.putExtra("artist_name", artistName);
        intent.putExtra("song_image_url", song.getImageUrl());
        intent.putExtra("song_spotify_url", song.getSpotifyUrl());

        startActivity(intent);
    }

    private static class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.SongViewHolder> {

        private final List<SpotifyTrack> songs;
        private final OnSongClickListener listener;

        public PlaylistSongsAdapter(List<SpotifyTrack> songs, OnSongClickListener listener) {
            this.songs = songs;
            this.listener = listener;
        }

        @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(16, 16, 16, 16);
            layout.setBackgroundResource(R.drawable.bg_playlist_card);

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 14);
            layout.setLayoutParams(params);

            ImageView image = new ImageView(parent.getContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(130, 130);
            image.setLayoutParams(imageParams);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setPadding(2, 2, 2, 2);

            LinearLayout textContainer = new LinearLayout(parent.getContext());
            textContainer.setOrientation(LinearLayout.VERTICAL);
            textContainer.setPadding(18, 0, 0, 0);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );

            textContainer.setLayoutParams(textParams);

            TextView title = new TextView(parent.getContext());
            title.setTextColor(0xFFFFFFFF);
            title.setTextSize(16);
            title.setTypeface(null, Typeface.BOLD);
            title.setMaxLines(2);

            TextView artist = new TextView(parent.getContext());
            artist.setTextColor(0xFFA7B6C2);
            artist.setTextSize(14);
            artist.setMaxLines(1);

            textContainer.addView(title);
            textContainer.addView(artist);

            layout.addView(image);
            layout.addView(textContainer);

            return new SongViewHolder(layout, image, title, artist);
        }

        @Override
        public void onBindViewHolder(SongViewHolder holder, int position) {
            SpotifyTrack song = songs.get(position);

            holder.title.setText(song.getName());

            String artistName = "Artista desconocido";

            if (song.getArtists() != null && !song.getArtists().isEmpty()) {
                SpotifyArtist artist = song.getArtists().get(0);

                if (artist != null && artist.getName() != null) {
                    artistName = artist.getName();
                }
            }

            holder.artist.setText(artistName);

            String imageUrl = song.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(imageUrl)
                        .fitCenter()
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.logo);
            }

            holder.itemView.setOnClickListener(v -> listener.onSongClick(song));
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        static class SongViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView title;
            TextView artist;

            public SongViewHolder(android.view.View itemView, ImageView image, TextView title, TextView artist) {
                super(itemView);
                this.image = image;
                this.title = title;
                this.artist = artist;
            }
        }

        interface OnSongClickListener {
            void onSongClick(SpotifyTrack song);
        }
    }
}