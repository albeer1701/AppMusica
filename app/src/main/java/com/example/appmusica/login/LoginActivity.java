package com.example.appmusica.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appmusica.MainActivity;
import com.example.appmusica.R;
import com.example.appmusica.api.AuthClient;
import com.example.appmusica.api.AuthService;
import com.example.appmusica.api.LoginRequest;
import com.example.appmusica.api.SessionManager;
import com.example.appmusica.api.SupabaseConfig;
import com.example.appmusica.api.UserSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            openMain();
            return;
        }

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> login());

        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Introduce el email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email no válido");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Introduce la contraseña");
            return;
        }

        btnLogin.setEnabled(false);

        AuthService service = AuthClient.getService();

        service.login(
                SupabaseConfig.API_KEY,
                "Bearer " + SupabaseConfig.API_KEY,
                new LoginRequest(email, password)
        ).enqueue(new Callback<List<UserSession>>() {
            @Override
            public void onResponse(Call<List<UserSession>> call, Response<List<UserSession>> response) {
                btnLogin.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(
                            LoginActivity.this,
                            "Error login BBDD. Código: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                List<UserSession> users = response.body();

                if (users == null || users.isEmpty()) {
                    Toast.makeText(
                            LoginActivity.this,
                            "Email o contraseña incorrectos",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                UserSession user = users.get(0);

                sessionManager.saveSession(
                        user.id,
                        user.email,
                        user.nombreUsuario,
                        user.rol
                );

                Toast.makeText(LoginActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                openMain();
            }

            @Override
            public void onFailure(Call<List<UserSession>> call, Throwable t) {
                btnLogin.setEnabled(true);

                Toast.makeText(
                        LoginActivity.this,
                        "Error conexión login: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void openMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}