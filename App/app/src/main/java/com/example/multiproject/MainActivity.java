package com.example.multiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbReference;
    TextView user_name, device, irrigation, weather, temper, stat;
    ImageButton settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        user_name = findViewById(R.id.textView5);
        device = findViewById(R.id.textView7);
        irrigation = findViewById(R.id.textView8);
        weather = findViewById(R.id.textView9);
        temper = findViewById(R.id.textView10);
        stat = findViewById(R.id.textView11);
        settings = findViewById(R.id.imageButton);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        device.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, DeviceActivity.class));
        });

        irrigation.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, IrrigationActivity.class));
        });

        weather.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, WeatherActivity.class));
        });

        temper.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, TemperatureActivity.class));
        });

        stat.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        });

        settings.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));}
        else {
            dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
            String uid = user.getUid();
            dbReference.child(uid).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) { }
                    else {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
            dbReference.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String full_name = snapshot.child("name").getValue().toString();
                    user_name.setText(full_name);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}