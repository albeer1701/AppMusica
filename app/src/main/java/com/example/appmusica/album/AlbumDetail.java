package com.example.appmusica.album;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmusica.R;
import com.example.appmusica.song.SongDetail;
import com.example.appmusica.spotify.SpotifyAlbumTracksResponse;
import com.example.appmusica.spotify.SpotifyApiService;
import com.example.appmusica.spotify.SpotifyArtist;
import com.example.appmusica.spotify.SpotifyTrack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlbumDetail extends AppCompatActivity {

    private Button btnBackAlbum;
    private Button btnSaveAlbumRating;
    private ImageView ivAlbumCover;
    private TextView tvAlbumTitle;
    private TextView tvAlbumArtist;
    private TextView tvAlbumAverageRating;
    private TextView tvAlbumCommentsList;
    private RatingBar ratingAlbum;
    private EditText etAlbumReview;
    private RecyclerView rvAlbumSongs;

    private SpotifyApiService spotifyApiService;

    private AlbumSongsAdapter albumSongsAdapter;
    private final List<SpotifyTrack> albumSongs = new ArrayList<>();

    private String albumId;
    private String albumName;
    private String albumArtist;
    private String albumImageUrl;
    private String spotifyAccessToken;

    private SharedPreferences globalAlbumPrefs;
    private SharedPreferences userProfilePrefs;

    private static final String SESSION_PREFS = "user_session";
    private static final String GLOBAL_ALBUM_PREFS = "album_ratings_global";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        btnBackAlbum = findViewById(R.id.btnBackAlbum);
        btnSaveAlbumRating = findViewById(R.id.btnSaveAlbumRating);
        ivAlbumCover = findViewById(R.id.ivAlbumCover);
        tvAlbumTitle = findViewById(R.id.tvAlbumTitle);
        tvAlbumArtist = findViewById(R.id.tvAlbumArtist);
        tvAlbumAverageRating = findViewById(R.id.tvAlbumAverageRating);
        tvAlbumCommentsList = findViewById(R.id.tvAlbumCommentsList);
        ratingAlbum = findViewById(R.id.ratingAlbum);
        etAlbumReview = findViewById(R.id.etAlbumReview);
        rvAlbumSongs = findViewById(R.id.rvAlbumSongs);

        globalAlbumPrefs = getSharedPreferences(GLOBAL_ALBUM_PREFS, MODE_PRIVATE);
        userProfilePrefs = getSharedPreferences(getUserProfilePrefsName(), MODE_PRIVATE);

        albumId = getIntent().getStringExtra("album_id");
        albumName = getIntent().getStringExtra("album_name");
        albumArtist = getIntent().getStringExtra("album_artist");
        albumImageUrl = getIntent().getStringExtra("album_image_url");
        spotifyAccessToken = getIntent().getStringExtra("spotify_access_token");

        if (albumId == null || albumId.isEmpty()) {
            albumId = "unknown_album";
        }

        if (albumName == null || albumName.isEmpty()) {
            albumName = "Álbum";
        }

        if (albumArtist == null || albumArtist.isEmpty()) {
            albumArtist = "Artista desconocido";
        }

        if (albumImageUrl == null) {
            albumImageUrl = "";
        }

        if (spotifyAccessToken == null) {
            spotifyAccessToken = "";
        }

        tvAlbumTitle.setText(albumName);
        tvAlbumArtist.setText(albumArtist);

        if (!albumImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(albumImageUrl)
                    .fitCenter()
                    .into(ivAlbumCover);
        } else {
            ivAlbumCover.setImageResource(R.drawable.logo);
        }

        btnBackAlbum.setOnClickListener(v -> finish());
        btnSaveAlbumRating.setOnClickListener(v -> saveAlbumRatingAndComment());

        setupRetrofit();
        setupRecyclerView();
        loadAlbumData();

        if (!spotifyAccessToken.isEmpty()) {
            loadAlbumSongs();
        } else {
            Toast.makeText(this, "Token de Spotify no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentUsername() {
        SharedPreferences sessionPrefs = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        String username = sessionPrefs.getString("username", "admin");

        if (username == null || username.trim().isEmpty()) {
            username = "admin";
        }

        return clean(username.trim());
    }

    private String getUserProfilePrefsName() {
        return "profile_ratings_" + getCurrentUsername();
    }

    private void setupRetrofit() {
        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        spotifyApiService = apiRetrofit.create(SpotifyApiService.class);
    }

    private void setupRecyclerView() {
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        albumSongsAdapter = new AlbumSongsAdapter(albumSongs, albumImageUrl, song -> openSongDetail(song));
        rvAlbumSongs.setAdapter(albumSongsAdapter);
    }

    private void loadAlbumSongs() {
        spotifyApiService.getAlbumTracks(
                "Bearer " + spotifyAccessToken,
                albumId,
                50,
                "ES"
        ).enqueue(new Callback<SpotifyAlbumTracksResponse>() {
            @Override
            public void onResponse(Call<SpotifyAlbumTracksResponse> call, Response<SpotifyAlbumTracksResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getItems() != null) {
                    albumSongs.clear();
                    albumSongs.addAll(response.body().getItems());
                    albumSongsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(
                            AlbumDetail.this,
                            "Error álbum Spotify " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<SpotifyAlbumTracksResponse> call, Throwable t) {
                Toast.makeText(
                        AlbumDetail.this,
                        "Fallo álbum Spotify: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void saveAlbumRatingAndComment() {
        float rating = ratingAlbum.getRating();
        String comment = etAlbumReview.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Selecciona una valoración", Toast.LENGTH_SHORT).show();
            return;
        }

        float currentTotal = globalAlbumPrefs.getFloat(getTotalKey(), 0f);
        int currentCount = globalAlbumPrefs.getInt(getCountKey(), 0);

        float newTotal = currentTotal + rating;
        int newCount = currentCount + 1;

        String oldComments = globalAlbumPrefs.getString(getCommentsKey(), "");

        String username = getCurrentUsername();

        String newComment;

        if (!comment.isEmpty()) {
            newComment = username + " ★ " + rating + "/5 - " + comment;
        } else {
            newComment = username + " ★ " + rating + "/5 - Sin comentario";
        }

        String updatedComments;

        if (oldComments == null || oldComments.isEmpty()) {
            updatedComments = newComment;
        } else {
            updatedComments = oldComments + "\n\n" + newComment;
        }

        globalAlbumPrefs.edit()
                .putFloat(getTotalKey(), newTotal)
                .putInt(getCountKey(), newCount)
                .putString(getCommentsKey(), updatedComments)
                .apply();

        saveAlbumInUserProfile(!comment.isEmpty());

        ratingAlbum.setRating(0);
        etAlbumReview.setText("");

        loadAlbumData();

        Toast.makeText(this, "Valoración guardada", Toast.LENGTH_SHORT).show();
    }

    private void saveAlbumInUserProfile(boolean newCommentAdded) {
        Set<String> savedAlbums = userProfilePrefs.getStringSet("profile_rated_albums", new HashSet<>());
        Set<String> updatedAlbums = new HashSet<>();

        boolean hadCommentBefore = false;

        if (savedAlbums != null) {
            for (String item : savedAlbums) {
                if (item != null && item.startsWith(albumId + "§")) {
                    String[] parts = item.split("§", -1);

                    if (parts.length >= 5 && parts[4].equals("true")) {
                        hadCommentBefore = true;
                    }
                } else if (item != null) {
                    updatedAlbums.add(item);
                }
            }
        }

        boolean hasComment = hadCommentBefore || newCommentAdded;

        String albumData =
                clean(albumId) + "§" +
                        clean(albumName) + "§" +
                        clean(albumArtist) + "§" +
                        clean(albumImageUrl) + "§" +
                        hasComment;

        updatedAlbums.add(albumData);

        userProfilePrefs.edit()
                .putStringSet("profile_rated_albums", updatedAlbums)
                .apply();
    }

    private void loadAlbumData() {
        float total = globalAlbumPrefs.getFloat(getTotalKey(), 0f);
        int count = globalAlbumPrefs.getInt(getCountKey(), 0);
        String comments = globalAlbumPrefs.getString(getCommentsKey(), "");

        if (count == 0) {
            tvAlbumAverageRating.setText("Media: sin valoraciones");
        } else {
            float average = total / count;
            tvAlbumAverageRating.setText("Media: " + String.format("%.1f", average) + "/5 (" + count + ")");
        }

        if (comments == null || comments.isEmpty()) {
            tvAlbumCommentsList.setText("Todavía no hay comentarios.");
        } else {
            tvAlbumCommentsList.setText(comments);
        }
    }

    private String getTotalKey() {
        return "album_" + albumId + "_total";
    }

    private String getCountKey() {
        return "album_" + albumId + "_count";
    }

    private String getCommentsKey() {
        return "album_" + albumId + "_comments";
    }

    private void openSongDetail(SpotifyTrack song) {
        String artistName = "Artista desconocido";

        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            SpotifyArtist artist = song.getArtists().get(0);

            if (artist != null && artist.getName() != null) {
                artistName = artist.getName();
            }
        }

        Intent intent = new Intent(AlbumDetail.this, SongDetail.class);
        intent.putExtra("song_id", song.getId());
        intent.putExtra("song_name", song.getName());
        intent.putExtra("artist_name", artistName);
        intent.putExtra("song_image_url", albumImageUrl);
        intent.putExtra("song_spotify_url", song.getSpotifyUrl());

        startActivity(intent);
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("§", " ")
                .replace(" ", "_");
    }

    private static class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.AlbumSongViewHolder> {

        private final List<SpotifyTrack> songs;
        private final String albumImageUrl;
        private final OnSongClickListener listener;

        AlbumSongsAdapter(List<SpotifyTrack> songs, String albumImageUrl, OnSongClickListener listener) {
            this.songs = songs;
            this.albumImageUrl = albumImageUrl;
            this.listener = listener;
        }

        @Override
        public AlbumSongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(8, 8, 8, 8);
            layout.setBackgroundResource(R.drawable.bg_playlist_card);

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 10);
            layout.setLayoutParams(params);

            ImageView image = new ImageView(parent.getContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            LinearLayout textContainer = new LinearLayout(parent.getContext());
            textContainer.setOrientation(LinearLayout.VERTICAL);
            textContainer.setPadding(14, 0, 0, 0);
            textContainer.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    )
            );

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

            return new AlbumSongViewHolder(layout, image, title, artist);
        }

        @Override
        public void onBindViewHolder(AlbumSongViewHolder holder, int position) {
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

            if (albumImageUrl != null && !albumImageUrl.isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(albumImageUrl)
                        .centerCrop()
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

        static class AlbumSongViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView title;
            TextView artist;

            AlbumSongViewHolder(android.view.View itemView, ImageView image, TextView title, TextView artist) {
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