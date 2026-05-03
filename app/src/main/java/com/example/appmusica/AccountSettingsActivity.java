package com.example.appmusica;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage;
    private Button btnChangePhoto, btnEditName, btnPrivacy, btnLogout;
    private TextView tvUsername, tvSongCount, tvAlbumCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        initViews();
        setupActions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        profileImage = findViewById(R.id.profileImage);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnEditName = findViewById(R.id.btnEditName);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnLogout = findViewById(R.id.btnLogout);

        tvUsername = findViewById(R.id.tvUsername);
        tvSongCount = findViewById(R.id.tvSongCount);
        tvAlbumCount = findViewById(R.id.tvAlbumCount);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());

        btnChangePhoto.setOnClickListener(v -> {
        });

        btnEditName.setOnClickListener(v -> {
        });

        btnPrivacy.setOnClickListener(v -> {
        });

        btnLogout.setOnClickListener(v -> {
        });
    }
}