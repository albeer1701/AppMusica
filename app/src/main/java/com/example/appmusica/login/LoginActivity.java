package com.example.appmusica.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appmusica.MainActivity;
import com.example.appmusica.R;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    private static final String USER_PREFS = "users_prefs";
    private static final String SESSION_PREFS = "user_session";

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "adminyourtune@gmail.com";
    private static final String ADMIN_PASSWORD = "Admin121992";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        createDefaultAdminIfNeeded();

        btnLogin.setOnClickListener(v -> login());

        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createDefaultAdminIfNeeded() {
        SharedPreferences usersPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        usersPrefs.edit()
                .putString("user_" + ADMIN_USERNAME, ADMIN_PASSWORD)
                .putString("email_" + ADMIN_USERNAME, ADMIN_EMAIL)
                .apply();
    }

    private void login() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Introduce tu email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Introduce tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = findUsernameByEmail(email);

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences usersPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String savedPassword = usersPrefs.getString("user_" + username, "");

        if (!password.equals(savedPassword)) {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sessionPrefs = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        sessionPrefs.edit()
                .putBoolean("is_logged", true)
                .putString("username", username)
                .putString("email", email)
                .apply();

        Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String findUsernameByEmail(String email) {
        SharedPreferences usersPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        Map<String, ?> allUsers = usersPrefs.getAll();

        for (Map.Entry<String, ?> entry : allUsers.entrySet()) {
            String key = entry.getKey();

            if (key.startsWith("email_")) {
                Object value = entry.getValue();

                if (value != null && email.equalsIgnoreCase(value.toString())) {
                    return key.replace("email_", "");
                }
            }
        }

        return "";
    }
}