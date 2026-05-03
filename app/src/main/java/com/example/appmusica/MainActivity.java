package com.example.appmusica;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout sectionHome, sectionSongs, sectionAlbums, sectionProfile;

    private ImageButton navHome;
    private Button navSongs, navAlbums, navProfile;

    private Button btnExploreMusic, btnMyLibrary;
    private Button btnSettings, btnPrivacy, btnLogout;

    private ImageView profileIcon;

    private EditText etSearchSongs, etSearchAlbums;
    private TextView tvSongCount, tvAlbumCount;

    private RecyclerView rvHomeRecent, rvSongs, rvAlbums;

    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private SongAdapter homeRecentAdapter;

    private final List<Song> allSongs = new ArrayList<>();
    private final List<Album> allAlbums = new ArrayList<>();

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && profileIcon != null) {
                        profileIcon.setImageURI(uri);
                    }
                }
        );

        initViews();
        setupData();
        setupRecyclerViews();
        setupNavigation();
        setupSearch();
        setupProfileButtons();
        updateProfileStats();
        showSection("home");
    }

    private void initViews() {
        sectionHome = findViewById(R.id.sectionHome);
        sectionSongs = findViewById(R.id.sectionSongs);
        sectionAlbums = findViewById(R.id.sectionAlbums);
        sectionProfile = findViewById(R.id.sectionProfile);

        navHome = findViewById(R.id.navHome);
        navSongs = findViewById(R.id.navSongs);
        navAlbums = findViewById(R.id.navAlbums);
        navProfile = findViewById(R.id.navProfile);

        btnExploreMusic = findViewById(R.id.btnExploreMusic);
        btnMyLibrary = findViewById(R.id.btnMyLibrary);

        profileIcon = findViewById(R.id.profileIcon);

        etSearchSongs = findViewById(R.id.etSearchSongs);
        etSearchAlbums = findViewById(R.id.etSearchAlbums);

        rvHomeRecent = findViewById(R.id.rvHomeRecent);
        rvSongs = findViewById(R.id.rvSongs);
        rvAlbums = findViewById(R.id.rvAlbums);

        tvSongCount = findViewById(R.id.tvSongCount);
        tvAlbumCount = findViewById(R.id.tvAlbumCount);

        btnSettings = findViewById(R.id.btnSettings);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupData() {
        allSongs.clear();
        allAlbums.clear();

        allSongs.add(new Song("Bohemian Rhapsody", "Queen", "A Night at the Opera", "5:55", 1975, "Rock", 4.9, "1.2k"));
        allSongs.add(new Song("Imagine", "John Lennon", "Imagine", "3:03", 1971, "Rock", 4.7, "856"));
        allSongs.add(new Song("Billie Jean", "Michael Jackson", "Thriller", "4:54", 1983, "Pop", 4.6, "1.1k"));
        allSongs.add(new Song("Smells Like Teen Spirit", "Nirvana", "Nevermind", "5:01", 1991, "Grunge", 4.8, "934"));
        allSongs.add(new Song("Fix You", "Coldplay", "X&Y", "4:55", 2005, "Rock", 3.0, "642"));
        allSongs.add(new Song("Friday I'm in Love", "The Cure", "Wish", "3:35", 1992, "Pop", 2.0, "388"));

        allAlbums.add(new Album("Abbey Road", "The Beatles", 1969, 17, "Rock", 4.9, "1.5k"));
        allAlbums.add(new Album("Thriller", "Michael Jackson", 1982, 9, "Pop", 4.7, "2.1k"));
        allAlbums.add(new Album("The Wall", "Pink Floyd", 1979, 26, "Progressive", 4.6, "987"));
    }

    private void setupRecyclerViews() {
        rvHomeRecent.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvAlbums.setLayoutManager(new LinearLayoutManager(this));

        List<Song> homeSongs = new ArrayList<>();
        homeSongs.add(allSongs.get(0));
        homeSongs.add(allSongs.get(4));
        homeSongs.add(allSongs.get(5));

        homeRecentAdapter = new SongAdapter(homeSongs, this::showSongDialog);
        rvHomeRecent.setAdapter(homeRecentAdapter);

        songAdapter = new SongAdapter(new ArrayList<>(allSongs), this::showSongDialog);
        rvSongs.setAdapter(songAdapter);

        albumAdapter = new AlbumAdapter(new ArrayList<>(allAlbums));
        rvAlbums.setAdapter(albumAdapter);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> showSection("home"));
        navSongs.setOnClickListener(v -> showSection("songs"));
        navAlbums.setOnClickListener(v -> showSection("albums"));
        navProfile.setOnClickListener(v -> showSection("profile"));

        btnExploreMusic.setOnClickListener(v -> showSection("songs"));
        btnMyLibrary.setOnClickListener(v -> showSection("albums"));

        profileIcon.setOnClickListener(v -> showAccountSettingsDialog());
    }

    private void setupSearch() {
        etSearchSongs.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }
        });

        etSearchAlbums.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAlbums(s.toString());
            }
        });
    }

    private void setupProfileButtons() {
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> showAccountSettingsDialog());
        }

        if (btnPrivacy != null) {
            btnPrivacy.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Privacidad")
                            .setMessage("Opciones de privacidad próximamente.")
                            .setPositiveButton("OK", null)
                            .show()
            );
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Cerrar sesión")
                            .setMessage("¿Seguro que quieres cerrar sesión?")
                            .setPositiveButton("Sí", (dialog, which) -> finish())
                            .setNegativeButton("Cancelar", null)
                            .show()
            );
        }
    }

    private void updateProfileStats() {
        if (tvSongCount != null) {
            tvSongCount.setText(String.valueOf(allSongs.size()));
        }
        if (tvAlbumCount != null) {
            tvAlbumCount.setText(String.valueOf(allAlbums.size()));
        }
    }

    private void filterSongs(String text) {
        List<Song> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();

        for (Song song : allSongs) {
            if (song.getTitle().toLowerCase().contains(query) ||
                    song.getArtist().toLowerCase().contains(query)) {
                filtered.add(song);
            }
        }

        songAdapter.updateList(filtered);
    }

    private void filterAlbums(String text) {
        List<Album> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();

        for (Album album : allAlbums) {
            if (album.getTitle().toLowerCase().contains(query) ||
                    album.getArtist().toLowerCase().contains(query)) {
                filtered.add(album);
            }
        }

        albumAdapter.updateList(filtered);
    }

    private void showSection(String section) {
        sectionHome.setVisibility(View.GONE);
        sectionSongs.setVisibility(View.GONE);
        sectionAlbums.setVisibility(View.GONE);
        sectionProfile.setVisibility(View.GONE);

        switch (section) {
            case "songs":
                sectionSongs.setVisibility(View.VISIBLE);
                break;
            case "albums":
                sectionAlbums.setVisibility(View.VISIBLE);
                break;
            case "profile":
                sectionProfile.setVisibility(View.VISIBLE);
                break;
            default:
                sectionHome.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showSongDialog(Song song) {
        String message =
                "Artista: " + song.getArtist() + "\n" +
                        "Álbum: " + song.getAlbum() + "\n" +
                        "Duración: " + song.getDuration() + "\n" +
                        "Año: " + song.getYear() + "\n" +
                        "Género: " + song.getGenre() + "\n" +
                        "Valoración: " + song.getRating() + "/5";

        new AlertDialog.Builder(this)
                .setTitle(song.getTitle())
                .setMessage(message)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showAccountSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Configuración de la cuenta")
                .setMessage("Aquí podrás gestionar tu cuenta.")
                .setPositiveButton("Cambiar foto", (dialog, which) -> pickImageLauncher.launch("image/*"))
                .setNegativeButton("Cerrar", null)
                .show();
    }
}