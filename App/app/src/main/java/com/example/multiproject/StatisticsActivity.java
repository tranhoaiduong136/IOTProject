package com.example.multiproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.*;

// Add more to get the latest from IO_Adafruit
import android.os.AsyncTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.*;

// Take 4 data : temperature, humdity, soil, light level
public class StatisticsActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName(); // for url request
    MQTTHelper mqttHelper; // pointer to mqtt helper class

    TextView moisture, temperature, humidity, light;
    ImageButton btnBack;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbReference;
    GraphView graph;
    LineGraphSeries<DataPoint> series_init;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        moisture = findViewById(R.id.moisture_data);
        temperature = findViewById(R.id.temperature_data);
        humidity = findViewById(R.id.humidity_data);
        light = findViewById(R.id.light_data);
        btnBack = findViewById(R.id.btnback2);

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(StatisticsActivity.this, MainActivity.class));
        });

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        graph = (GraphView) findViewById(R.id.graph);

        new GetContacts().execute();
        startMQTT();
    }
    String[] init_temp = {"30", "20", "29", "25","31"};
    int count_temp = 5;
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(StatisticsActivity.this, LoginActivity.class));}
        else {
            dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
            String uid = user.getUid();
            dbReference.child(uid).child("Device").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String moisture_data = snapshot.child("moisture").getValue().toString();
                    moisture.setText(moisture_data);
                    String temperature_data = snapshot.child("temperature").getValue().toString();
                    temperature.setText(temperature_data);
                    String humidity_data = snapshot.child("humidity").getValue().toString();
                    humidity.setText(humidity_data);
                    String light_data = snapshot.child("light").getValue().toString();
                    light.setText(light_data);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            // Temp
            String url = "https://io.adafruit.com/api/v2/duong_bku_13/feeds/bbc-temp/data";
            String jsonStr = sh.makeServiceCall(url);
            String url2 = "https://io.adafruit.com/api/v2/duong_bku_13/feeds/bbc-humid/data";
            String jsonStrAir = sh.makeServiceCall(url2);
            String url3 = "https://io.adafruit.com/api/v2/duong_bku_13/feeds/bbc-pressure/data";
            String jsonStrLight = sh.makeServiceCall(url3);
            String url4 = "https://io.adafruit.com/api/v2/duong_bku_13/feeds/bbc-wind-speed/data";
            String jsonStrHumid = sh.makeServiceCall(url4);
            if (jsonStr != null && jsonStrAir != null && jsonStrLight != null && jsonStrHumid != null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObj = null;
                        try {
                            jsonObj = new JSONArray(jsonStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            for(int i = 0; i < 5; ++i){
                                init_temp[i] = jsonObj.getJSONObject(i).getString("value");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        temperature.setText(init_temp[0] + "°C");
                        series_init = new LineGraphSeries<>(new DataPoint[] {
                                new DataPoint(0, Integer.parseInt(init_temp[0])),
                                new DataPoint(1, Integer.parseInt(init_temp[1])),
                                new DataPoint(2, Integer.parseInt(init_temp[2])),
                                new DataPoint(3, Integer.parseInt(init_temp[3])),
                                new DataPoint(4, Integer.parseInt(init_temp[4])),
                        });
                        graph.addSeries(series_init);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObjAir = null;
                        try {
                            jsonObjAir = new JSONArray(jsonStrAir);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String curr = null;
                        try {
                            curr = jsonObjAir.getJSONObject(0).getString("value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        moisture.setText(curr + "%");
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObjLight = null;
                        try {
                            jsonObjLight = new JSONArray(jsonStrLight);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String curr = null;
                        try {
                            curr = jsonObjLight.getJSONObject(0).getString("value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        light.setText(curr + "");
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObjHumid = null;
                        try {
                            jsonObjHumid = new JSONArray(jsonStrHumid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String curr = null;
                        try {
                            curr = jsonObjHumid.getJSONObject(0).getString("value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        humidity.setText(curr + "");
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
                if(topic.contains("bbc-temp")) {
                    Log.d("mqtt", "Received Temperature: " + message.toString());
                    temperature.setText(message.toString() + "°C");
                    DataPoint add = new DataPoint(count_temp,Integer.parseInt(message.toString()));
                    series_init.appendData(add,true,40);
                    graph.addSeries(series_init);
                    count_temp = count_temp + 1 ;
                }
                else if(topic.contains("bbc-humid")) {
                    Log.d("mqtt", "Received Humidity: " + message.toString());
                    moisture.setText(message.toString() + "%");
                }
                else if(topic.contains("bbc-wind-speed")) {
                    Log.d("mqtt", "Received Wind Speed: " + message.toString());
                    humidity.setText(message.toString() + "");
                }
                else if(topic.contains("bbc-pressure")) {
                    Log.d("mqtt", "Received Pressure: " + message.toString());
                    light.setText(message.toString() + "");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("mqtt", "Deliver Successfully");
            }
        });

    }

}