package com.example.multiproject;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

// Add more to get the latest from IO_Adafruit
import android.os.AsyncTask;
import org.json.*;



public class TemperatureActivity extends AppCompatActivity {
    // Duong add
    private String TAG = MainActivity.class.getSimpleName(); // for url request
    MQTTHelper mqttHelper; // pointer to mqtt helper class

    RadioGroup group;
    ImageButton btnBack;
    TextView num, lower, upper, celsius;
    SeekBar seekBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbReference;
    EditText low, up;
    Button tmpSave;
    SwitchCompat tempSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        btnBack = findViewById(R.id.btnback12);
        lower = findViewById(R.id.textView30);
        upper = findViewById(R.id.textView31);
        num = findViewById(R.id.textView32);
        seekBar = findViewById(R.id.seekBar8);
        low = findViewById(R.id.editTextNumberDecimal2);
        up = findViewById(R.id.editTextNumberDecimal3);
        tmpSave = findViewById(R.id.tempSaveBtn);
        celsius = findViewById(R.id.textCel);
        tempSwitch = findViewById(R.id.tempSwitch);
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(TemperatureActivity.this, MainActivity.class));
        });
        group = findViewById(R.id.radioGroup);
        seekBar.setVisibility(View.INVISIBLE);
        num.setVisibility(View.INVISIBLE);
        celsius.setVisibility(View.INVISIBLE);
        tempSwitch.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();
        dbReference.child("Device").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.child("temp_level").getValue().toString();
                seekBar.setProgress(Integer.valueOf(temp));
                num.setText(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton:
                        lower.setVisibility(View.INVISIBLE);
                        upper.setVisibility(View.INVISIBLE);
                        low.setVisibility(View.INVISIBLE);
                        up.setVisibility(View.INVISIBLE);
                        seekBar.setVisibility(View.VISIBLE);
                        num.setVisibility(View.VISIBLE);
                        celsius.setVisibility(View.VISIBLE);
                        tempSwitch.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton2:
                        lower.setVisibility(View.VISIBLE);
                        upper.setVisibility(View.VISIBLE);
                        low.setVisibility(View.VISIBLE);
                        up.setVisibility(View.VISIBLE);
                        seekBar.setVisibility(View.INVISIBLE);
                        num.setVisibility(View.INVISIBLE);
                        celsius.setVisibility(View.INVISIBLE);
                        tempSwitch.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int process, boolean b) {
                num.setText(String.valueOf(process));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    sendDataMQTT("duong_bku_13/feeds/bbc-led","1");
                    Log.d("mqtt","On LED server");
                } else {
                    // The toggle is disabled
                    sendDataMQTT("duong_bku_13/feeds/bbc-led","0");
                    Log.d("mqtt","Off LED server");
                }
            }
        });

        tmpSave.setOnClickListener(view -> saveAbove());
        // Duong add
        new GetContacts().execute();
        startMQTT();
    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://io.adafruit.com/api/v2/duong_bku_13/feeds/bbc-led/data";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObj = null;
                        try {
                            jsonObj = new JSONArray(jsonStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Getting JSON Array node
                        String curr = null;
                        try {
                            curr = jsonObj.getJSONObject(0).getString("value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(curr.contains("1")){
                            tempSwitch.setChecked(true);
                        }else{
                            tempSwitch.setChecked(false);
                        }
                    }
                });

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private void startMQTT() {
        mqttHelper = new MQTTHelper(getApplicationContext(), "123456789");
        mqttHelper.setCallback(new MqttCallbackExtended(){
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqtt", "Connection is successful"); // Log debug in logcat
                mqttHelper.subscribeToTopic();
            }
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("mqtt", "Lost Connection!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", "Received: " + message.toString());
                if(topic.contains("bbc-led")){
                    Log.d("mqtt", "The led is " + ((message.toString().contains("1"))?"ON":"OFF"));
                    if(message.toString().contains("1")){
                        tempSwitch.setChecked(true);
                    }else{
                        tempSwitch.setChecked(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("mqtt", "Deliver Successfully");
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(TemperatureActivity.this, LoginActivity.class));}
        else {
            dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
            String uid = user.getUid();
            dbReference.child(uid);
        }
    }

    private void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();

        msg.setId(1234);
        msg.setQos(0);          // set quality of service (0 -> 4); higher, more trustful, but slower
        msg.setRetained(true);  // when a new client connect to this topic, he will receive the latest data

        byte[] b = value.getBytes(Charset.forName("UTF-8")); // Change all to byte
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (Exception e){}
    }
    protected void saveAbove() {
            dbReference = FirebaseDatabase.getInstance().getReference();
            dbReference.child("Device").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dbReference.child("Device").child("temp_level").setValue(seekBar.getProgress());
                    String t_min = low.getText().toString();
                    String t_max = up.getText().toString();
                    if (TextUtils.isEmpty(low.getText())){
                        t_min = snapshot.child("temp_min").getValue().toString();
                    }
                    if (TextUtils.isEmpty(up.getText())) {
                        t_max = snapshot.child("temp_max").getValue().toString();
                    }
                    int temp_min = Integer.valueOf(t_min);
                    int temp_max = Integer.valueOf(t_max);
                    dbReference.child("Device").child("temp_min").setValue(temp_min);
                    dbReference.child("Device").child("temp_max").setValue(temp_max);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            startActivity(new Intent(TemperatureActivity.this, MainActivity.class));
    }

}