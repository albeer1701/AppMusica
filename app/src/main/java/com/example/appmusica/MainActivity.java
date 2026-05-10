package com.example.appmusica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
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

import com.bumptech.glide.Glide;
import com.example.appmusica.spotify.SpotifyAlbum;
import com.example.appmusica.spotify.SpotifyAlbumSearchResponse;
import com.example.appmusica.spotify.SpotifyApiService;
import com.example.appmusica.spotify.SpotifyArtist;
import com.example.appmusica.spotify.SpotifyAuthService;
import com.example.appmusica.spotify.SpotifyPlaylist;
import com.example.appmusica.spotify.SpotifyPlaylistSearchResponse;
import com.example.appmusica.spotify.SpotifySearchResponse;
import com.example.appmusica.spotify.SpotifyTokenResponse;
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

public class MainActivity extends AppCompatActivity {

    private ImageButton navHome;
    private Button navSongs;
    private Button navAlbums;
    private Button navProfile;
    private Button btnExploreMusic;
    private Button btnMyLibrary;

    private ImageView profileIcon;

    private Button btnSortBest;
    private Button btnSortWorst;

    private LinearLayout sectionHome;
    private LinearLayout sectionSongs;
    private LinearLayout sectionAlbums;
    private LinearLayout sectionProfile;

    private EditText etSearchSongs;
    private EditText etSearchAlbums;

    private RecyclerView rvSongs;
    private RecyclerView rvAlbums;
    private RecyclerView rvHomeRecent;
    private RecyclerView rvRatedSongs;
    private RecyclerView rvCommentedSongs;
    private RecyclerView rvRatedAlbums;
    private RecyclerView rvCommentedAlbums;

    private SpotifyApiService spotifyApiService;
    private SpotifyAuthService spotifyAuthService;

    private String spotifyAccessToken = "";

    private SpotifySongAdapter songAdapter;
    private final List<SpotifyTrack> songList = new ArrayList<>();

    private SpotifyAlbumAdapter albumAdapter;
    private final List<SpotifyAlbum> albumList = new ArrayList<>();

    private HomePlaylistAdapter homePlaylistAdapter;
    private final List<SpotifyPlaylist> homePlaylistList = new ArrayList<>();

    private ProfileItemAdapter ratedSongsAdapter;
    private ProfileItemAdapter commentedSongsAdapter;
    private ProfileItemAdapter ratedAlbumsAdapter;
    private ProfileItemAdapter commentedAlbumsAdapter;

    private final List<ProfileItem> ratedSongsList = new ArrayList<>();
    private final List<ProfileItem> commentedSongsList = new ArrayList<>();
    private final List<ProfileItem> ratedAlbumsList = new ArrayList<>();
    private final List<ProfileItem> commentedAlbumsList = new ArrayList<>();

    private boolean sortBestFirst = true;

    private ActivityResultLauncher<String[]> profileImagePicker;

    private static final String SESSION_PREFS = "user_session";
    private static final String PROFILE_IMAGE_URI = "profile_image_uri";

    private static final String GLOBAL_SONG_PREFS = "song_ratings_global";
    private static final String GLOBAL_ALBUM_PREFS = "album_ratings_global";

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

        profileIcon = findViewById(R.id.profileIcon);

        btnExploreMusic = findViewById(R.id.btnExploreMusic);
        btnMyLibrary = findViewById(R.id.btnMyLibrary);

        btnSortBest = findViewById(R.id.btnSortBest);
        btnSortWorst = findViewById(R.id.btnSortWorst);

        sectionHome = findViewById(R.id.sectionHome);
        sectionSongs = findViewById(R.id.sectionSongs);
        sectionAlbums = findViewById(R.id.sectionAlbums);
        sectionProfile = findViewById(R.id.sectionProfile);

        etSearchSongs = findViewById(R.id.etSearchSongs);
        etSearchAlbums = findViewById(R.id.etSearchAlbums);

        rvSongs = findViewById(R.id.rvSongs);
        rvAlbums = findViewById(R.id.rvAlbums);
        rvHomeRecent = findViewById(R.id.rvHomeRecent);
        rvRatedSongs = findViewById(R.id.rvRatedSongs);
        rvCommentedSongs = findViewById(R.id.rvCommentedSongs);
        rvRatedAlbums = findViewById(R.id.rvRatedAlbums);
        rvCommentedAlbums = findViewById(R.id.rvCommentedAlbums);

        setupProfileImagePicker();
        setupRecyclerViews();
        setupSpotifyRetrofit();
        getSpotifyToken();
        setupNavigation();
        setupSearch();
        loadSavedProfileImage();
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

    private String getProfileImagePrefsName() {
        return "profile_image_" + getCurrentUsername();
    }

    private void setupProfileImagePicker() {
        profileImagePicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception ignored) {
                        }

                        SharedPreferences prefs = getSharedPreferences(getProfileImagePrefsName(), MODE_PRIVATE);
                        prefs.edit()
                                .putString(PROFILE_IMAGE_URI, uri.toString())
                                .apply();

                        profileIcon.setImageURI(uri);
                        Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadSavedProfileImage() {
        SharedPreferences prefs = getSharedPreferences(getProfileImagePrefsName(), MODE_PRIVATE);
        String savedUri = prefs.getString(PROFILE_IMAGE_URI, "");

        if (savedUri != null && !savedUri.isEmpty()) {
            profileIcon.setImageURI(Uri.parse(savedUri));
        } else {
            profileIcon.setImageResource(R.drawable.logo);
        }
    }

    private void setupRecyclerViews() {
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SpotifySongAdapter(songList, song -> openSongDetail(song));
        rvSongs.setAdapter(songAdapter);

        rvAlbums.setLayoutManager(new LinearLayoutManager(this));
        albumAdapter = new SpotifyAlbumAdapter(albumList, album -> openAlbumDetail(album));
        rvAlbums.setAdapter(albumAdapter);

        rvHomeRecent.setLayoutManager(new LinearLayoutManager(this));
        homePlaylistAdapter = new HomePlaylistAdapter(homePlaylistList, playlist -> openPlaylistDetail(playlist));
        rvHomeRecent.setAdapter(homePlaylistAdapter);

        rvRatedSongs.setLayoutManager(new LinearLayoutManager(this));
        ratedSongsAdapter = new ProfileItemAdapter(ratedSongsList, item -> openProfileItem(item));
        rvRatedSongs.setAdapter(ratedSongsAdapter);

        rvCommentedSongs.setLayoutManager(new LinearLayoutManager(this));
        commentedSongsAdapter = new ProfileItemAdapter(commentedSongsList, item -> openProfileItem(item));
        rvCommentedSongs.setAdapter(commentedSongsAdapter);

        rvRatedAlbums.setLayoutManager(new LinearLayoutManager(this));
        ratedAlbumsAdapter = new ProfileItemAdapter(ratedAlbumsList, item -> openProfileItem(item));
        rvRatedAlbums.setAdapter(ratedAlbumsAdapter);

        rvCommentedAlbums.setLayoutManager(new LinearLayoutManager(this));
        commentedAlbumsAdapter = new ProfileItemAdapter(commentedAlbumsList, item -> openProfileItem(item));
        rvCommentedAlbums.setAdapter(commentedAlbumsAdapter);
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

        navAlbums.setOnClickListener(v -> {
            showSection(sectionAlbums);

            if (spotifyAccessToken == null || spotifyAccessToken.isEmpty()) {
                Toast.makeText(this, "Token de Spotify todavía no cargado", Toast.LENGTH_SHORT).show();
            } else {
                searchSpotifyAlbums("popular albums");
            }
        });

        navProfile.setOnClickListener(v -> {
            showSection(sectionProfile);
            loadProfileData();
        });

        profileIcon.setOnClickListener(v -> showAccountMenu());

        btnSortBest.setOnClickListener(v -> {
            sortBestFirst = true;
            loadProfileData();
        });

        btnSortWorst.setOnClickListener(v -> {
            sortBestFirst = false;
            loadProfileData();
        });

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

    private void showAccountMenu() {
        String[] options = {
                "Cambiar foto de perfil",
                "Configuración",
                "Privacidad",
                "Cerrar sesión"
        };

        new AlertDialog.Builder(this)
                .setTitle("Cuenta")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openGalleryForProfileImage();
                    } else if (which == 1) {
                        showSettingsDialog();
                    } else if (which == 2) {
                        showPrivacyDialog();
                    } else if (which == 3) {
                        confirmLogout();
                    }
                })
                .show();
    }

    private void openGalleryForProfileImage() {
        profileImagePicker.launch(new String[]{"image/*"});
    }

    private void showSettingsDialog() {
        String[] settings = {
                "Tema oscuro: activado",
                "Notificaciones: activadas",
                "Idioma: Español",
                "Reproducción externa: Spotify",
                "Orden del perfil: mejor/peor valoración"
        };

        new AlertDialog.Builder(this)
                .setTitle("Configuración")
                .setItems(settings, (dialog, which) ->
                        Toast.makeText(this, "Opción de configuración", Toast.LENGTH_SHORT).show()
                )
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showPrivacyDialog() {
        String privacyText =
                "Política de privacidad de YourTune\n\n" +
                        "YourTune guarda localmente tus valoraciones, comentarios y preferencias dentro del dispositivo.\n\n" +
                        "Las canciones y álbumes tienen valoraciones globales visibles para todos los usuarios.\n\n" +
                        "El perfil de cada usuario solo muestra las canciones y álbumes que ese usuario ha valorado o comentado.\n\n" +
                        "La aplicación usa la API de Spotify para mostrar canciones, álbumes, playlists, portadas y enlaces externos.\n\n" +
                        "No se venden datos personales ni se comparten tus valoraciones con terceros desde esta versión de la app.\n\n" +
                        "La foto de perfil seleccionada se guarda únicamente como referencia local en el dispositivo.\n\n" +
                        "Al cerrar sesión, se vuelve a la pantalla de crear cuenta.";

        new AlertDialog.Builder(this)
                .setTitle("Privacidad")
                .setMessage(privacyText)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (dialog, which) -> logout())
                .show();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(MainActivity.this, com.example.appmusica.login.RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

        etSearchAlbums.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.length() >= 2 && spotifyAccessToken != null && !spotifyAccessToken.isEmpty()) {
                    searchSpotifyAlbums(query);
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
                    loadHomePlaylists();
                } else {
                    showTokenError(response);
                }
            }

            @Override
            public void onFailure(Call<SpotifyTokenResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo token Spotify: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchSpotifySongs(String query) {
        query = query.trim();

        if (query.isEmpty()) {
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
                } else {
                    showSpotifySearchError(response);
                }
            }

            @Override
            public void onFailure(Call<SpotifySearchResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo búsqueda Spotify: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchSpotifyAlbums(String query) {
        query = query.trim();

        if (query.isEmpty()) {
            return;
        }

        spotifyApiService.searchAlbums(
                "Bearer " + spotifyAccessToken,
                query,
                "album",
                10,
                "ES"
        ).enqueue(new Callback<SpotifyAlbumSearchResponse>() {
            @Override
            public void onResponse(Call<SpotifyAlbumSearchResponse> call, Response<SpotifyAlbumSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getAlbums() != null
                        && response.body().getAlbums().getItems() != null) {

                    albumList.clear();

                    for (SpotifyAlbum album : response.body().getAlbums().getItems()) {
                        if (album != null) {
                            albumList.add(album);
                        }
                    }

                    albumAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Error álbumes Spotify: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SpotifyAlbumSearchResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo álbumes Spotify: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadHomePlaylists() {
        if (spotifyAccessToken == null || spotifyAccessToken.isEmpty()) {
            return;
        }

        homePlaylistList.clear();
        homePlaylistAdapter.notifyDataSetChanged();

        loadHomePlaylistsByQuery("top hits");
        loadHomePlaylistsByQuery("popular songs");
        loadHomePlaylistsByQuery("reggaeton");
        loadHomePlaylistsByQuery("pop");
        loadHomePlaylistsByQuery("rock");
        loadHomePlaylistsByQuery("chill");
        loadHomePlaylistsByQuery("latin");
        loadHomePlaylistsByQuery("party");
    }

    private void loadHomePlaylistsByQuery(String query) {
        spotifyApiService.searchPlaylists(
                "Bearer " + spotifyAccessToken,
                query,
                "playlist",
                10,
                "ES"
        ).enqueue(new Callback<SpotifyPlaylistSearchResponse>() {
            @Override
            public void onResponse(Call<SpotifyPlaylistSearchResponse> call, Response<SpotifyPlaylistSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getPlaylists() != null
                        && response.body().getPlaylists().getItems() != null) {

                    for (SpotifyPlaylist playlist : response.body().getPlaylists().getItems()) {
                        if (playlist != null && !playlistAlreadyExists(playlist.getId())) {
                            homePlaylistList.add(playlist);
                        }
                    }

                    homePlaylistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SpotifyPlaylistSearchResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo playlists Spotify: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean playlistAlreadyExists(String playlistId) {
        if (playlistId == null) {
            return true;
        }

        for (SpotifyPlaylist playlist : homePlaylistList) {
            if (playlist != null && playlistId.equals(playlist.getId())) {
                return true;
            }
        }

        return false;
    }

    private void loadProfileData() {
        loadProfileSongs();
        loadProfileAlbums();
    }

    private void loadProfileSongs() {
        SharedPreferences profilePrefs = getSharedPreferences(getUserProfilePrefsName(), MODE_PRIVATE);
        SharedPreferences globalSongPrefs = getSharedPreferences(GLOBAL_SONG_PREFS, MODE_PRIVATE);

        Set<String> savedSongs = profilePrefs.getStringSet("profile_rated_songs", new HashSet<>());

        ratedSongsList.clear();
        commentedSongsList.clear();

        if (savedSongs != null) {
            for (String item : savedSongs) {
                String[] parts = item.split("§", -1);

                if (parts.length < 5) {
                    continue;
                }

                String id = parts[0];
                String name = parts[1];
                String artist = parts[2];
                String imageUrl = parts[3];
                boolean hasComment = parts[4].equals("true");

                float total = globalSongPrefs.getFloat("song_" + id + "_total", 0f);
                int count = globalSongPrefs.getInt("song_" + id + "_count", 0);

                if (count == 0) {
                    continue;
                }

                ProfileItem profileItem = new ProfileItem(
                        "song",
                        id,
                        name,
                        artist,
                        imageUrl,
                        total / count,
                        count
                );

                ratedSongsList.add(profileItem);

                if (hasComment) {
                    commentedSongsList.add(profileItem);
                }
            }
        }

        sortProfileList(ratedSongsList);
        sortProfileList(commentedSongsList);

        ratedSongsAdapter.notifyDataSetChanged();
        commentedSongsAdapter.notifyDataSetChanged();
    }

    private void loadProfileAlbums() {
        SharedPreferences profilePrefs = getSharedPreferences(getUserProfilePrefsName(), MODE_PRIVATE);
        SharedPreferences globalAlbumPrefs = getSharedPreferences(GLOBAL_ALBUM_PREFS, MODE_PRIVATE);

        Set<String> savedAlbums = profilePrefs.getStringSet("profile_rated_albums", new HashSet<>());

        ratedAlbumsList.clear();
        commentedAlbumsList.clear();

        if (savedAlbums != null) {
            for (String item : savedAlbums) {
                String[] parts = item.split("§", -1);

                if (parts.length < 5) {
                    continue;
                }

                String id = parts[0];
                String name = parts[1];
                String artist = parts[2];
                String imageUrl = parts[3];
                boolean hasComment = parts[4].equals("true");

                float total = globalAlbumPrefs.getFloat("album_" + id + "_total", 0f);
                int count = globalAlbumPrefs.getInt("album_" + id + "_count", 0);

                if (count == 0) {
                    continue;
                }

                ProfileItem profileItem = new ProfileItem(
                        "album",
                        id,
                        name,
                        artist,
                        imageUrl,
                        total / count,
                        count
                );

                ratedAlbumsList.add(profileItem);

                if (hasComment) {
                    commentedAlbumsList.add(profileItem);
                }
            }
        }

        sortProfileList(ratedAlbumsList);
        sortProfileList(commentedAlbumsList);

        ratedAlbumsAdapter.notifyDataSetChanged();
        commentedAlbumsAdapter.notifyDataSetChanged();
    }

    private void sortProfileList(List<ProfileItem> list) {
        if (sortBestFirst) {
            list.sort((a, b) -> Float.compare(b.average, a.average));
        } else {
            list.sort((a, b) -> Float.compare(a.average, b.average));
        }
    }

    private void showTokenError(Response<SpotifyTokenResponse> response) {
        Toast.makeText(MainActivity.this, "Error token Spotify: " + response.code(), Toast.LENGTH_LONG).show();
    }

    private void showSpotifySearchError(Response<SpotifySearchResponse> response) {
        Toast.makeText(MainActivity.this, "Error Spotify: " + response.code(), Toast.LENGTH_LONG).show();
    }

    private void openProfileItem(ProfileItem item) {
        if (item == null) {
            return;
        }

        if ("song".equals(item.type)) {
            Intent intent = new Intent(MainActivity.this, SongDetail.class);
            intent.putExtra("song_id", item.id);
            intent.putExtra("song_name", item.name);
            intent.putExtra("artist_name", item.artist);
            intent.putExtra("song_image_url", item.imageUrl);
            intent.putExtra("song_spotify_url", "");
            startActivity(intent);
        } else if ("album".equals(item.type)) {
            Intent intent = new Intent(MainActivity.this, AlbumDetail.class);
            intent.putExtra("album_id", item.id);
            intent.putExtra("album_name", item.name);
            intent.putExtra("album_artist", item.artist);
            intent.putExtra("album_image_url", item.imageUrl);
            intent.putExtra("spotify_access_token", spotifyAccessToken);
            startActivity(intent);
        }
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
        intent.putExtra("song_image_url", song.getImageUrl());
        intent.putExtra("song_spotify_url", song.getSpotifyUrl());

        startActivity(intent);
    }

    private void openAlbumDetail(SpotifyAlbum album) {
        Intent intent = new Intent(MainActivity.this, AlbumDetail.class);
        intent.putExtra("album_id", album.getId());
        intent.putExtra("album_name", album.getName());
        intent.putExtra("album_artist", album.getMainArtistName());
        intent.putExtra("album_image_url", album.getImageUrl());
        intent.putExtra("spotify_access_token", spotifyAccessToken);

        startActivity(intent);
    }

    private void openPlaylistDetail(SpotifyPlaylist playlist) {
        Intent intent = new Intent(MainActivity.this, PlaylistDetail.class);
        intent.putExtra("playlist_id", playlist.getId());
        intent.putExtra("playlist_name", playlist.getName());
        intent.putExtra("playlist_image_url", playlist.getImageUrl());
        intent.putExtra("spotify_access_token", spotifyAccessToken);

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

    private static class ProfileItem {

        String type;
        String id;
        String name;
        String artist;
        String imageUrl;
        float average;
        int count;

        ProfileItem(String type, String id, String name, String artist, String imageUrl, float average, int count) {
            this.type = type;
            this.id = id;
            this.name = name;
            this.artist = artist;
            this.imageUrl = imageUrl;
            this.average = average;
            this.count = count;
        }
    }

    private static class ProfileItemAdapter extends RecyclerView.Adapter<ProfileItemAdapter.ProfileItemViewHolder> {

        private final List<ProfileItem> items;
        private final OnProfileItemClickListener listener;

        ProfileItemAdapter(List<ProfileItem> items, OnProfileItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override
        public ProfileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView title = new TextView(parent.getContext());
            title.setTextColor(0xFFFFFFFF);
            title.setTextSize(16);
            title.setTypeface(null, Typeface.BOLD);
            title.setMaxLines(2);

            TextView artist = new TextView(parent.getContext());
            artist.setTextColor(0xFFA7B6C2);
            artist.setTextSize(14);
            artist.setMaxLines(1);

            TextView rating = new TextView(parent.getContext());
            rating.setTextColor(0xFFFFFFFF);
            rating.setTextSize(14);
            rating.setTypeface(null, Typeface.BOLD);

            textContainer.addView(title);
            textContainer.addView(artist);
            textContainer.addView(rating);

            layout.addView(image);
            layout.addView(textContainer);

            return new ProfileItemViewHolder(layout, image, title, artist, rating);
        }

        @Override
        public void onBindViewHolder(ProfileItemViewHolder holder, int position) {
            ProfileItem item = items.get(position);

            holder.title.setText(item.name);
            holder.artist.setText(item.artist);
            holder.rating.setText("Media: " + String.format("%.1f", item.average) + "/5 (" + item.count + ")");

            if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(item.imageUrl)
                        .centerCrop()
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.logo);
            }

            holder.itemView.setOnClickListener(v -> listener.onProfileItemClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ProfileItemViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView title;
            TextView artist;
            TextView rating;

            ProfileItemViewHolder(View itemView, ImageView image, TextView title, TextView artist, TextView rating) {
                super(itemView);
                this.image = image;
                this.title = title;
                this.artist = artist;
                this.rating = rating;
            }
        }

        interface OnProfileItemClickListener {
            void onProfileItemClick(ProfileItem item);
        }
    }

    private static class SpotifySongAdapter extends RecyclerView.Adapter<SpotifySongAdapter.SongViewHolder> {

        private final List<SpotifyTrack> songs;
        private final OnSongClickListener listener;

        SpotifySongAdapter(List<SpotifyTrack> songs, OnSongClickListener listener) {
            this.songs = songs;
            this.listener = listener;
        }

        @Override
        public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView title = new TextView(parent.getContext());
            title.setTextColor(0xFFFFFFFF);
            title.setTextSize(17);
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

            if (song.getArtists() != null && !song.getArtists().isEmpty() && song.getArtists().get(0) != null) {
                SpotifyArtist artist = song.getArtists().get(0);

                if (artist.getName() != null) {
                    artistName = artist.getName();
                }
            }

            holder.artist.setText(artistName);

            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(song.getImageUrl())
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

        static class SongViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView artist;

            SongViewHolder(View itemView, ImageView image, TextView title, TextView artist) {
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

    private static class SpotifyAlbumAdapter extends RecyclerView.Adapter<SpotifyAlbumAdapter.AlbumViewHolder> {

        private final List<SpotifyAlbum> albums;
        private final OnAlbumClickListener listener;

        SpotifyAlbumAdapter(List<SpotifyAlbum> albums, OnAlbumClickListener listener) {
            this.albums = albums;
            this.listener = listener;
        }

        @Override
        public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView title = new TextView(parent.getContext());
            title.setTextColor(0xFFFFFFFF);
            title.setTextSize(17);
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

            return new AlbumViewHolder(layout, image, title, artist);
        }

        @Override
        public void onBindViewHolder(AlbumViewHolder holder, int position) {
            SpotifyAlbum album = albums.get(position);

            holder.title.setText(album.getName());
            holder.artist.setText(album.getMainArtistName());

            if (album.getImageUrl() != null && !album.getImageUrl().isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(album.getImageUrl())
                        .centerCrop()
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.logo);
            }

            holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
        }

        @Override
        public int getItemCount() {
            return albums.size();
        }

        static class AlbumViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView artist;

            AlbumViewHolder(View itemView, ImageView image, TextView title, TextView artist) {
                super(itemView);
                this.image = image;
                this.title = title;
                this.artist = artist;
            }
        }

        interface OnAlbumClickListener {
            void onAlbumClick(SpotifyAlbum album);
        }
    }

    private static class HomePlaylistAdapter extends RecyclerView.Adapter<HomePlaylistAdapter.PlaylistViewHolder> {

        private final List<SpotifyPlaylist> playlists;
        private final OnPlaylistClickListener listener;

        HomePlaylistAdapter(List<SpotifyPlaylist> playlists, OnPlaylistClickListener listener) {
            this.playlists = playlists;
            this.listener = listener;
        }

        @Override
        public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LinearLayout textContainer = new LinearLayout(parent.getContext());
            textContainer.setOrientation(LinearLayout.VERTICAL);
            textContainer.setPadding(14, 0, 0, 0);
            textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView title = new TextView(parent.getContext());
            title.setTextColor(0xFFFFFFFF);
            title.setTextSize(17);
            title.setTypeface(null, Typeface.BOLD);
            title.setMaxLines(2);

            TextView description = new TextView(parent.getContext());
            description.setTextColor(0xFFA7B6C2);
            description.setTextSize(13);
            description.setMaxLines(3);

            textContainer.addView(title);
            textContainer.addView(description);

            layout.addView(image);
            layout.addView(textContainer);

            return new PlaylistViewHolder(layout, image, title, description);
        }

        @Override
        public void onBindViewHolder(PlaylistViewHolder holder, int position) {
            SpotifyPlaylist playlist = playlists.get(position);

            holder.title.setText(playlist.getName());

            if (playlist.getDescription() == null || playlist.getDescription().trim().isEmpty()) {
                holder.description.setText("Playlist de Spotify");
            } else {
                holder.description.setText(playlist.getDescription());
            }

            if (playlist.getImageUrl() != null && !playlist.getImageUrl().isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(playlist.getImageUrl())
                        .fitCenter()
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.logo);
            }

            holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
        }

        @Override
        public int getItemCount() {
            return playlists.size();
        }

        static class PlaylistViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView description;

            PlaylistViewHolder(View itemView, ImageView image, TextView title, TextView description) {
                super(itemView);
                this.image = image;
                this.title = title;
                this.description = description;
            }
        }

        interface OnPlaylistClickListener {
            void onPlaylistClick(SpotifyPlaylist playlist);
        }
    }
}