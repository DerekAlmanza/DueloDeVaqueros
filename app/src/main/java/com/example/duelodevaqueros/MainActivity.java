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

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Handler;
import android.view.WindowManager;
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
        gunView = findViewById(R.id.gun_iv);
        start_button = findViewById(R.id.start_button);/**initialize the view with the botton start**/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if(stepDetectorSensor == null){
            sensorManager = null;
        }
    }

    /**metodo to pause the aplicacion**/
    @Override
    protected void onPause() {
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    /**method to restart the app*/
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    /**
     * method to execute the activity when entries it has focus or not**/
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    /**
     * Method to initialize the aplication and presents the start button
     * Auxiliar method. Start the duel.
     */
    private void init(){
        gunView.setVisibility(View.INVISIBLE);
        start_button .setVisibility(View.VISIBLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(!checkActivityRecognitionPermission()){
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
    }

    /**
    * Method to check if we have permission to use the sensor.
    */
    @TargetApi(29)
    private boolean checkActivityRecognitionPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }
  

    /**
     * method linked to the principal view, recieves an object of type view and we make the button invisible when it's touched**/
    public void finalCountdown(View startButton){
        startButton.setUiVisibility(View.INVISIBLE);
        checkStepSensor();
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