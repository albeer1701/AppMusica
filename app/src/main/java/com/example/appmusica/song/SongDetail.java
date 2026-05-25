package com.example.appmusica.song;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appmusica.R;

import java.util.HashSet;
import java.util.Set;

public class SongDetail extends AppCompatActivity {

    private ImageView ivSongCover;
    private TextView tvSongTitle;
    private TextView tvSongArtist;
    private TextView tvAverageRating;
    private TextView tvCommentsList;
    private RatingBar ratingSong;
    private EditText etSongReview;
    private Button btnSaveRating;
    private Button btnBack;
    private Button btnOpenSpotify;

    private String songId;
    private String songName;
    private String artistName;
    private String imageUrl;
    private String spotifyUrl;

    private SharedPreferences globalSongPrefs;
    private SharedPreferences userProfilePrefs;

    private static final String SESSION_PREFS = "user_session";
    private static final String GLOBAL_SONG_PREFS = "song_ratings_global";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        ivSongCover = findViewById(R.id.ivSongCover);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvSongArtist = findViewById(R.id.tvSongArtist);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvCommentsList = findViewById(R.id.tvCommentsList);
        ratingSong = findViewById(R.id.ratingSong);
        etSongReview = findViewById(R.id.etSongReview);
        btnSaveRating = findViewById(R.id.btnSaveRating);
        btnBack = findViewById(R.id.btnBack);
        btnOpenSpotify = findViewById(R.id.btnOpenSpotify);

        configurarEstrellasDoradas(ratingSong);

        globalSongPrefs = getSharedPreferences(GLOBAL_SONG_PREFS, MODE_PRIVATE);
        userProfilePrefs = getSharedPreferences(getUserProfilePrefsName(), MODE_PRIVATE);

        songId = getIntent().getStringExtra("song_id");
        songName = getIntent().getStringExtra("song_name");
        artistName = getIntent().getStringExtra("artist_name");
        imageUrl = getIntent().getStringExtra("song_image_url");
        spotifyUrl = getIntent().getStringExtra("song_spotify_url");

        if (songId == null || songId.isEmpty()) {
            songId = "unknown_song";
        }

        if (songName == null || songName.isEmpty()) {
            songName = "Canción desconocida";
        }

        if (artistName == null || artistName.isEmpty()) {
            artistName = "Artista desconocido";
        }

        if (imageUrl == null) {
            imageUrl = "";
        }

        if (spotifyUrl == null) {
            spotifyUrl = "";
        }

        tvSongTitle.setText(songName);
        tvSongArtist.setText(artistName);

        if (!imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(ivSongCover);
        } else {
            ivSongCover.setImageResource(R.drawable.logo);
        }

        loadSongData();

        btnBack.setOnClickListener(v -> finish());
        btnOpenSpotify.setOnClickListener(v -> openSpotifySong());
        btnSaveRating.setOnClickListener(v -> saveRatingAndComment());
    }

    private void configurarEstrellasDoradas(RatingBar ratingBar) {
        int dorado = Color.parseColor("#FFD700");
        int doradoHover = Color.parseColor("#FFC107");
        int fondoEstrella = Color.parseColor("#1B3148");

        ratingBar.setProgressTintList(ColorStateList.valueOf(dorado));
        ratingBar.setSecondaryProgressTintList(ColorStateList.valueOf(dorado));
        ratingBar.setProgressBackgroundTintList(ColorStateList.valueOf(fondoEstrella));
        ratingBar.setThumbTintList(ColorStateList.valueOf(dorado));

        ratingBar.setOnHoverListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER ||
                    event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {

                ratingBar.setProgressTintList(ColorStateList.valueOf(doradoHover));
                ratingBar.setSecondaryProgressTintList(ColorStateList.valueOf(doradoHover));
                ratingBar.setThumbTintList(ColorStateList.valueOf(doradoHover));

            } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {

                ratingBar.setProgressTintList(ColorStateList.valueOf(dorado));
                ratingBar.setSecondaryProgressTintList(ColorStateList.valueOf(dorado));
                ratingBar.setThumbTintList(ColorStateList.valueOf(dorado));
            }

            return false;
        });
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

    private void openSpotifySong() {
        if (spotifyUrl == null || spotifyUrl.isEmpty()) {
            Toast.makeText(this, "No hay enlace de Spotify para esta canción", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl));
        startActivity(intent);
    }

    private void saveRatingAndComment() {
        float rating = ratingSong.getRating();
        String comment = etSongReview.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Selecciona una valoración", Toast.LENGTH_SHORT).show();
            return;
        }

        float currentTotal = globalSongPrefs.getFloat(getTotalKey(), 0f);
        int currentCount = globalSongPrefs.getInt(getCountKey(), 0);

        float newTotal = currentTotal + rating;
        int newCount = currentCount + 1;

        String oldComments = globalSongPrefs.getString(getCommentsKey(), "");

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

        globalSongPrefs.edit()
                .putFloat(getTotalKey(), newTotal)
                .putInt(getCountKey(), newCount)
                .putString(getCommentsKey(), updatedComments)
                .apply();

        saveSongInUserProfile(!comment.isEmpty());

        ratingSong.setRating(0);
        etSongReview.setText("");

        loadSongData();

        Toast.makeText(this, "Valoración guardada", Toast.LENGTH_SHORT).show();
    }

    private void saveSongInUserProfile(boolean newCommentAdded) {
        Set<String> savedSongs = userProfilePrefs.getStringSet("profile_rated_songs", new HashSet<>());
        Set<String> updatedSongs = new HashSet<>();

        boolean hadCommentBefore = false;

        if (savedSongs != null) {
            for (String item : savedSongs) {
                if (item != null && item.startsWith(songId + "§")) {
                    String[] parts = item.split("§", -1);

                    if (parts.length >= 5 && parts[4].equals("true")) {
                        hadCommentBefore = true;
                    }
                } else if (item != null) {
                    updatedSongs.add(item);
                }
            }
        }

        boolean hasComment = hadCommentBefore || newCommentAdded;

        String songData =
                clean(songId) + "§" +
                        clean(songName) + "§" +
                        clean(artistName) + "§" +
                        clean(imageUrl) + "§" +
                        hasComment;

        updatedSongs.add(songData);

        userProfilePrefs.edit()
                .putStringSet("profile_rated_songs", updatedSongs)
                .apply();
    }

    private void loadSongData() {
        float total = globalSongPrefs.getFloat(getTotalKey(), 0f);
        int count = globalSongPrefs.getInt(getCountKey(), 0);
        String comments = globalSongPrefs.getString(getCommentsKey(), "");

        if (count == 0) {
            tvAverageRating.setText("Media: sin valoraciones");
        } else {
            float average = total / count;
            tvAverageRating.setText("Media: " + String.format("%.1f", average) + "/5 (" + count + ")");
        }

        if (comments == null || comments.isEmpty()) {
            tvCommentsList.setText("Todavía no hay comentarios.");
        } else {
            tvCommentsList.setText(comments);
        }
    }

    private String getTotalKey() {
        return "song_" + songId + "_total";
    }

    private String getCountKey() {
        return "song_" + songId + "_count";
    }

    private String getCommentsKey() {
        return "song_" + songId + "_comments";
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("§", " ")
                .replace(" ", "_");
    }
}