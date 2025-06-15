package com.s23010210.ravindu;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class temp extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView tempText;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private final float TEMPERATURE_THRESHOLD = 10.0f;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        tempText = findViewById(R.id.tempText);
        stopButton = findViewById(R.id.stopButton);

        // Load and configure media player
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_alart);
        mediaPlayer.setLooping(true); // Play continuously

        stopButton.setOnClickListener(view -> {
            if (isPlaying) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0); // Reset to beginning
                isPlaying = false;

            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // ⚠️ Use a fallback sensor if temperature sensor is not available
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

            if (temperatureSensor == null) {
                Toast.makeText(this, "Ambient Temperature Sensor not available.\nUsing light sensor instead.", Toast.LENGTH_LONG).show();
                temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // fallback for testing
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null)
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float temp = event.values[0];
        Log.d("TEMP_SENSOR", "Sensor value: " + temp);

        tempText.setText("Current Temp: " + temp + "°C");

        if (temp > TEMPERATURE_THRESHOLD && !isPlaying) {
            isPlaying = true;
            mediaPlayer.start();
            Toast.makeText(this, "Temperature is high ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
