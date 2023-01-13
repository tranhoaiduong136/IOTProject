package com.example.multiproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class DeviceActivity extends AppCompatActivity {

    TextView dvSoil;
    TextView dvLight;
    TextView dvDHT;
    TextView dvPump;
    TextView dvLED;
    TextView dvDRV;
    TextView dvLCD;
    TextView dvReplay;
    ImageButton btnBack;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        getSupportActionBar().hide();

        dvSoil = findViewById(R.id.dvSoil);
        dvLight = findViewById(R.id.dvLight);
        dvDHT = findViewById(R.id.dvDHT);
        dvPump = findViewById(R.id.dvPump);
        dvLED = findViewById(R.id.dvLED);
        dvDRV = findViewById(R.id.dvDRV);
        dvLCD = findViewById(R.id.dvLCD);
        dvReplay = findViewById(R.id.dvReplay);
        btnBack = findViewById(R.id.btnbackDevice);

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(DeviceActivity.this, MainActivity.class));
        });

        mAuth = FirebaseAuth.getInstance();

        dvSoil.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, SoilActivity.class));
        });
        dvLight.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, LightActivity.class));
        });
        dvDHT.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, DHTActivity.class));
        });
        dvPump.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, PumpActivity.class));
        });
        dvLED.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, LEDActivity.class));
        });
        dvDRV.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, DRVActivity.class));
        });
        dvLCD.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, LCDActivity.class));
        });
        dvReplay.setOnClickListener(view ->{
            startActivity(new Intent(DeviceActivity.this, ReplayActivity.class));
        });
    }

}