package com.example.duelodevaqueros;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView gunView;
    private Button startButton;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private byte step;
    private ExecutorService singleThreadProducer;
    private DrawTimer asyncCounter;
    public static final byte SECONDS_TO_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Auxiliar method. Start the duel.
    private void init() {
        gunView.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(!checkActivityRecognitionPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
    }

    // Check if the permissions are conceed or not.
    @TargetApi(29)
    private boolean checkActivityRecognitionPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkStepSensor() {
        if (sensorManager == null) {
            startTimer();
            return;
        }
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Called when the accuracy of the registered sensor has changed.
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        step++;
        if(step >= 3){
            sensorManager.unregisterListener(this);
            gunView.setVisibility(View.VISIBLE);
            step = 0;
        }
    }

    // Run an async counter.
    private void startTimer() {
        if(singleThreadProducer == null) {
            singleThreadProducer = Executors.newSingleThreadExecutor();
        }
        asyncCounter = new DrawTimer(gunView, SECONDS_TO_COUNT);
        singleThreadProducer.execute(asyncCounter);
    }

    // Called when there is a new sensor event.
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}