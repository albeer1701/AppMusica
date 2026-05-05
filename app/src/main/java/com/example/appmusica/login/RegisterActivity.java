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
import com.example.appmusica.api.RegisterRequest;
import com.example.appmusica.api.SessionManager;
import com.example.appmusica.api.SupabaseConfig;
import com.example.appmusica.api.UserSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnRegister;
    private TextView tvGoLogin;

    private SessionManager sessionManager;

    private String pendingEmail;
    private String pendingPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etRegisterUsername);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> register());
        tvGoLogin.setOnClickListener(v -> finish());
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        pendingEmail = etEmail.getText().toString().trim();
        pendingPassword = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Introduce un nombre de usuario");
            return;
        }

        if (pendingEmail.isEmpty()) {
            etEmail.setError("Introduce el email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(pendingEmail).matches()) {
            etEmail.setError("Email no válido");
            return;
        }

        if (pendingPassword.isEmpty()) {
            etPassword.setError("Introduce la contraseña");
            return;
        }

        if (pendingPassword.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            return;
        }

        btnRegister.setEnabled(false);

        AuthService service = AuthClient.getService();

        service.register(
                SupabaseConfig.API_KEY,
                "Bearer " + SupabaseConfig.API_KEY,
                new RegisterRequest(pendingEmail, pendingPassword, username)
        ).enqueue(new Callback<List<UserSession>>() {
            @Override
            public void onResponse(Call<List<UserSession>> call, Response<List<UserSession>> response) {
                btnRegister.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Error registro BBDD. Código: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                List<UserSession> users = response.body();

                if (users == null || users.isEmpty()) {
                    Toast.makeText(
                            RegisterActivity.this,
                            "No se pudo crear el usuario",
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

                Toast.makeText(RegisterActivity.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                openMain();
            }

            @Override
            public void onFailure(Call<List<UserSession>> call, Throwable t) {
                btnRegister.setEnabled(true);

                Toast.makeText(
                        RegisterActivity.this,
                        "Error conexión registro: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void openMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}