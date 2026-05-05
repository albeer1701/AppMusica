package com.example.appmusica.api;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "yourtune_session";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROL = "rol";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String userId, String email, String username, String rol) {
        preferences.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROL, rol)
                .apply();
    }

    public boolean isLoggedIn() {
        String userId = preferences.getString(KEY_USER_ID, null);
        return userId != null && !userId.isEmpty();
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public String getRol() {
        return preferences.getString(KEY_ROL, "USER");
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getRol());
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}