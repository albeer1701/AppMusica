package com.example.appmusica;

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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmusica.api.AlbumRemote;
import com.example.appmusica.api.SongRemote;
import com.example.appmusica.api.SupabaseClient;
import com.example.appmusica.api.SupabaseConfig;
import com.example.appmusica.api.SupabaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        setupRecyclerViews();
        setupNavigation();
        setupSearch();
        setupProfileButtons();
        showSection("home");

        loadSongsFromSupabase();
        loadAlbumsFromSupabase();
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

    private void setupRecyclerViews() {
        if (rvHomeRecent != null) {
            rvHomeRecent.setLayoutManager(new LinearLayoutManager(this));
            homeRecentAdapter = new SongAdapter(new ArrayList<>(), this::showSongDialog);
            rvHomeRecent.setAdapter(homeRecentAdapter);
        }

        if (rvSongs != null) {
            rvSongs.setLayoutManager(new LinearLayoutManager(this));
            songAdapter = new SongAdapter(new ArrayList<>(), this::showSongDialog);
            rvSongs.setAdapter(songAdapter);
        }

        if (rvAlbums != null) {
            rvAlbums.setLayoutManager(new LinearLayoutManager(this));
            albumAdapter = new AlbumAdapter(new ArrayList<>());
            rvAlbums.setAdapter(albumAdapter);
        }
    }

    private void loadSongsFromSupabase() {
        SupabaseService service = SupabaseClient.getService();

        service.getCanciones(
                SupabaseConfig.API_KEY,
                SupabaseClient.getAuthorizationHeader()
        ).enqueue(new Callback<List<SongRemote>>() {
            @Override
            public void onResponse(Call<List<SongRemote>> call, Response<List<SongRemote>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(
                            MainActivity.this,
                            "Error canciones Supabase: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                List<SongRemote> remoteSongs = response.body();

                if (remoteSongs == null) {
                    Toast.makeText(
                            MainActivity.this,
                            "Supabase devuelve canciones NULL",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                allSongs.clear();

                for (SongRemote remote : remoteSongs) {
                    Song song = new Song(
                            safeText(remote.titulo),
                            safeText(remote.artista),
                            safeText(remote.album),
                            safeText(remote.duracion),
                            remote.anio,
                            safeText(remote.genero),
                            remote.notaMedia,
                            safeText(remote.numeroValoraciones)
                    );

                    allSongs.add(song);
                }

                if (songAdapter != null) {
                    songAdapter.updateList(new ArrayList<>(allSongs));
                }

                if (homeRecentAdapter != null) {
                    List<Song> homeSongs = new ArrayList<>();

                    for (int i = 0; i < allSongs.size() && i < 3; i++) {
                        homeSongs.add(allSongs.get(i));
                    }

                    homeRecentAdapter.updateList(homeSongs);
                }

                updateProfileStats();

                Toast.makeText(
                        MainActivity.this,
                        "Canciones desde Supabase: " + allSongs.size(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onFailure(Call<List<SongRemote>> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Fallo conexión canciones: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void loadAlbumsFromSupabase() {
        SupabaseService service = SupabaseClient.getService();

        service.getAlbumes(
                SupabaseConfig.API_KEY,
                SupabaseClient.getAuthorizationHeader()
        ).enqueue(new Callback<List<AlbumRemote>>() {
            @Override
            public void onResponse(Call<List<AlbumRemote>> call, Response<List<AlbumRemote>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(
                            MainActivity.this,
                            "Error álbumes Supabase: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                List<AlbumRemote> remoteAlbums = response.body();

                if (remoteAlbums == null) {
                    Toast.makeText(
                            MainActivity.this,
                            "Supabase devuelve álbumes NULL",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                allAlbums.clear();

                for (AlbumRemote remote : remoteAlbums) {
                    Album album = new Album(
                            safeText(remote.titulo),
                            safeText(remote.artista),
                            remote.anio,
                            remote.numeroCanciones,
                            safeText(remote.genero),
                            remote.notaMedia,
                            safeText(remote.numeroValoraciones)
                    );

                    allAlbums.add(album);
                }

                if (albumAdapter != null) {
                    albumAdapter.updateList(new ArrayList<>(allAlbums));
                }

                updateProfileStats();

                Toast.makeText(
                        MainActivity.this,
                        "Álbumes desde Supabase: " + allAlbums.size(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onFailure(Call<List<AlbumRemote>> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Fallo conexión álbumes: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private String safeText(String text) {
        return text != null ? text : "";
    }

    private void setupNavigation() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> showSection("home"));
        }

        if (navSongs != null) {
            navSongs.setOnClickListener(v -> showSection("songs"));
        }

        if (navAlbums != null) {
            navAlbums.setOnClickListener(v -> showSection("albums"));
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> showSection("profile"));
        }

        if (btnExploreMusic != null) {
            btnExploreMusic.setOnClickListener(v -> showSection("songs"));
        }

        if (btnMyLibrary != null) {
            btnMyLibrary.setOnClickListener(v -> showSection("albums"));
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> showAccountSettingsDialog());
        }
    }

    private void setupSearch() {
        if (etSearchSongs != null) {
            etSearchSongs.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterSongs(s.toString());
                }
            });
        }

        if (etSearchAlbums != null) {
            etSearchAlbums.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterAlbums(s.toString());
                }
            });
        }
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
        if (songAdapter == null) return;

        List<Song> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();

        for (Song song : allSongs) {
            if (song.getTitle().toLowerCase().contains(query) ||
                    song.getArtist().toLowerCase().contains(query) ||
                    song.getAlbum().toLowerCase().contains(query) ||
                    song.getGenre().toLowerCase().contains(query)) {
                filtered.add(song);
            }
        }

        songAdapter.updateList(filtered);
    }

    private void filterAlbums(String text) {
        if (albumAdapter == null) return;

        List<Album> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();

        for (Album album : allAlbums) {
            if (album.getTitle().toLowerCase().contains(query) ||
                    album.getArtist().toLowerCase().contains(query) ||
                    album.getGenre().toLowerCase().contains(query)) {
                filtered.add(album);
            }
        }

        albumAdapter.updateList(filtered);
    }

    private void showSection(String section) {
        if (sectionHome != null) sectionHome.setVisibility(View.GONE);
        if (sectionSongs != null) sectionSongs.setVisibility(View.GONE);
        if (sectionAlbums != null) sectionAlbums.setVisibility(View.GONE);
        if (sectionProfile != null) sectionProfile.setVisibility(View.GONE);

        switch (section) {
            case "songs":
                if (sectionSongs != null) {
                    sectionSongs.setVisibility(View.VISIBLE);
                }
                break;

            case "albums":
                if (sectionAlbums != null) {
                    sectionAlbums.setVisibility(View.VISIBLE);
                }
                break;

            case "profile":
                if (sectionProfile != null) {
                    sectionProfile.setVisibility(View.VISIBLE);
                }
                break;

            default:
                if (sectionHome != null) {
                    sectionHome.setVisibility(View.VISIBLE);
                }
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
                .setPositiveButton("Cambiar foto", (dialog, which) -> {
                    if (pickImageLauncher != null) {
                        pickImageLauncher.launch("image/*");
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }
}