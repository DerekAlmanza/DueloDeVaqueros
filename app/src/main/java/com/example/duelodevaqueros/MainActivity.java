package com.example.duelodevaqueros;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.view.WindowManager;
import java.util.concurrent.Executors;

import java.util.concurrent.ExecutorService;

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
        startButton = findViewById(R.id.start_button);/**initialize the view with the botton start**/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if(stepDetectorSensor == null){
            sensorManager = null;
        }
    }

    /**method to pause the application**/
    @Override
    protected void onPause() {
        killCounter();
        super.onPause();
    }

    /*
    * Restart the duel with the method onResume and call init() again.
    * */
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
  
    /**
     * Method that allows to run the app in full screen  using Inmersive mode.
     * A touch gesture will display the notification bar and system buttons, which
     * are hidden to allow the app to be shown in full screen.
     * @param hasFocus indicates if the screen is being focus.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    /**
     * Method to initialize the aplication and presents the start button
     * Auxiliar method. Start the duel.
     */
    private void init(){
        gunView.setVisibility(View.INVISIBLE);
        startButton .setVisibility(View.VISIBLE);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startButton.setVisibility(View.INVISIBLE);
        checkStepSensor();
    }

    private void checkStepSensor() {
        if (sensorManager == null) {
            startTimer();
            return;
        }
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
  
    /*
    * Occupies a handler to execute after 3000 milliseconds (3 seconds)
    * to restart the duel.
    * */
    public void fire(View gun) {
        JobIntentService.enqueueWork(this, SoundPlayer.class,
            SoundPlayer.JOB_ID, new Intent(SoundPlayer.ACTION_FIRE));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { init(); }
        }, 3000);
    }
  
    /*
    * Ends the account, whether there is a pedometer or not.
    * if there is a pedometer we say that the listener stops listening to the sensor,
    * otherwise, the thread ends and it becomes null.
    * */
    private void killCounter() {
        if(sensorManager != null) {
            sensorManager.unregisterListener(this);
        } else if(singleThreadProducer != null) {
            singleThreadProducer.shutdownNow();
            singleThreadProducer = null;
        }
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
