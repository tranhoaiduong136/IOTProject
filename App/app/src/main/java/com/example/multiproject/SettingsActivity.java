package com.example.multiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    TextView btnLogout;
    TextView btnEditprofile;
    ImageButton btnBack;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogOut);
        btnEditprofile = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnLogout.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        });
        btnEditprofile.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
        });
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        });
    }
}