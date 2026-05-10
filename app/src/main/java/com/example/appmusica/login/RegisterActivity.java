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

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterUsername;
    private EditText etRegisterEmail;
    private EditText etRegisterPassword;
    private Button btnRegister;
    private TextView tvGoLogin;

    private static final String USER_PREFS = "users_prefs";
    private static final String SESSION_PREFS = "user_session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> createAccount());

        tvGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createAccount() {
        String username = etRegisterUsername.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Introduce un email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Introduce una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences usersPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        if (usersPrefs.contains("user_" + username)) {
            Toast.makeText(this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show();
            return;
        }

        usersPrefs.edit()
                .putString("user_" + username, password)
                .putString("email_" + username, email)
                .apply();

        SharedPreferences sessionPrefs = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        sessionPrefs.edit()
                .putBoolean("is_logged", true)
                .putString("username", username)
                .apply();

        Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}