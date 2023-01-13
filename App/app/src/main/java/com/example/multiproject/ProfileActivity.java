package com.example.multiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    TextInputEditText full_name, job, phone;
    Button btnUpdate;
    DatabaseReference dbReference;
    FirebaseUser user;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        full_name = findViewById(R.id.etFullname);
        job = findViewById(R.id.etJob);
        phone = findViewById(R.id.etPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        btnUpdate.setOnClickListener(view -> updateProfile());

    }
    private void updateProfile() {
        String name = full_name.getText().toString();
        String j = job.getText().toString();
        String p = phone.getText().toString();
        if (TextUtils.isEmpty(name)){
            full_name.setError("Full name cannot be empty");
            full_name.requestFocus();
        }else if (TextUtils.isEmpty(j)){
            job.setError("Job cannot be empty");
            job.requestFocus();
        }else if (TextUtils.isEmpty(p)){
            phone.setError("Phone number cannot be empty");
            phone.requestFocus();
        }else {
            String uid = user.getUid();
            Users users = new Users(name, j, p);
            Device device = new Device();
            dbReference.child("Users").child(uid).child("Profile").setValue(users);
            dbReference.child("Device").setValue(device);
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        }
    }
}