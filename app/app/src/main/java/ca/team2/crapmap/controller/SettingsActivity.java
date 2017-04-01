package ca.team2.crapmap.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ca.team2.crapmap.R;

public class SettingsActivity extends AppCompatActivity {

    View imageView;
    View nameView;
    View logoutView;
    View loginView;
    View registerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imageView = findViewById(R.id.settings_user_icon);
        nameView = findViewById(R.id.settings_current_user);
        logoutView = findViewById(R.id.settings_logout);
        loginView = findViewById(R.id.settings_login);
        registerView = findViewById(R.id.settings_register);

        toggleViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        toggleViews();
    }

    void toggleViews() {
        SharedPreferences settings = getSharedPreferences("LOGIN_TOKEN", 0);
        String name = settings.getString("name", null);

        boolean loggedIn = name != null;

        ((TextView) nameView).setText(name);

        imageView.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        nameView.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        logoutView.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        loginView.setVisibility(!loggedIn ? View.VISIBLE : View.GONE);
        registerView.setVisibility(!loggedIn ? View.VISIBLE : View.GONE);
    }

    public void logout(View view) {
        SharedPreferences settings = getSharedPreferences("LOGIN_TOKEN", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("token");
        editor.remove("name");
        editor.commit();
        toggleViews();
    }

    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 200);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
