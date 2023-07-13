package com.example.budgetfoods.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

public class RememberMe {
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public RememberMe(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLoginCredentials(String username, String password) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public void clearLoginCredentials() {
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

    public String getSavedUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getSavedPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }
}