package com.example.multiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class IrrigationActivity extends AppCompatActivity {

    // Duong add
    private String TAG = MainActivity.class.getSimpleName(); // for url request
    MQTTHelper mqttHelper; // pointer to mqtt helper class

    TextView irrAbv, lower, upper, ml;
    ImageButton btnBack;
    EditText low, up;
    Button btnSave;
    SeekBar moistAbove;
    FirebaseUser user;
    DatabaseReference dbReference;
    RadioGroup group;
    SwitchCompat moistSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation);
        btnBack = findViewById(R.id.btnbackIrr);
        lower = findViewById(R.id.textLower);
        upper = findViewById(R.id.textUpper);
        low = findViewById(R.id.editIrrMin);
        up = findViewById(R.id.editIrrMax);
        moistAbove = findViewById(R.id.seekBarIrr);
        irrAbv = findViewById(R.id.textMoisture);
        btnSave = findViewById(R.id.irrSaveBtn);
        group = findViewById(R.id.radioGroupIrr);
        moistSwitch = findViewById(R.id.moistSwitch);
        ml = findViewById(R.id.textML);
        moistAbove.setVisibility(View.INVISIBLE);
        irrAbv.setVisibility(View.INVISIBLE);
        ml.setVisibility(View.INVISIBLE);
        moistSwitch.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(IrrigationActivity.this, MainActivity.class));
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();
        dbReference.child("Device").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String moisture = snapshot.child("moist_level").getValue().toString();
                moistAbove.setProgress(Integer.valueOf(moisture));
                irrAbv.setText(moisture);
                String unit = "ml/m" + "\u00B2";
                ml.setText(unit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        moistAbove.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                irrAbv.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioAutoIrr:
                        lower.setVisibility(View.INVISIBLE);
                        upper.setVisibility(View.INVISIBLE);
                        low.setVisibility(View.INVISIBLE);
                        up.setVisibility(View.INVISIBLE);
                        moistAbove.setVisibility(View.VISIBLE);
                        irrAbv.setVisibility(View.VISIBLE);
                        ml.setVisibility(View.VISIBLE);
                        moistSwitch.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioManualIrr:
                        lower.setVisibility(View.VISIBLE);
                        upper.setVisibility(View.VISIBLE);
                        low.setVisibility(View.VISIBLE);
                        up.setVisibility(View.VISIBLE);
                        moistAbove.setVisibility(View.INVISIBLE);
                        irrAbv.setVisibility(View.INVISIBLE);
                        ml.setVisibility(View.INVISIBLE);
                        moistSwitch.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        moistSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    sendDataMQTT("duong_bku_13/feeds/bbc-motor","1");
                    Log.d("mqtt","On Motor server");
                } else {
                    // The toggle is disabled
                    sendDataMQTT("duong_bku_13/feeds/bbc-motor","0");
                    Log.d("mqtt","Off Motor server");
                }
            }
        });
        btnSave.setOnClickListener(view -> saveAbove());
        // Duong add
        new GetContacts().execute();
        startMQTT();
    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://io.adafruit.com/api/v2/duong_bku_13/feeds/bbc-motor/data";
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
                            moistSwitch.setChecked(true);
                        }else{
                            moistSwitch.setChecked(false);
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
    @Override
    protected void onStart() {
        super.onStart();
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
                if(topic.contains("bbc-motor")){
                    Log.d("mqtt", "The motor is " + ((message.toString().contains("1"))?"ON":"OFF"));
                    if(message.toString().contains("1")){
                        moistSwitch.setChecked(true);
                    }else{
                        moistSwitch.setChecked(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("mqtt", "Deliver Successfully");
            }
        });

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
                    String m_min = low.getText().toString();
                    String m_max = up.getText().toString();
                    if (TextUtils.isEmpty(low.getText())){
                        m_min = snapshot.child("moist_min").getValue().toString();
                    }
                    if (TextUtils.isEmpty(up.getText())) {
                        m_max = snapshot.child("moist_max").getValue().toString();
                    }
                    int moist_min = Integer.valueOf(m_min);
                    int moist_max = Integer.valueOf(m_max);

                    dbReference.child("Device").child("moist_min").setValue(moist_min);
                    dbReference.child("Device").child("moist_max").setValue(moist_max);
                    dbReference.child("Device").child("moist_level").setValue(moistAbove.getProgress());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            startActivity(new Intent(IrrigationActivity.this, MainActivity.class));

    }
}