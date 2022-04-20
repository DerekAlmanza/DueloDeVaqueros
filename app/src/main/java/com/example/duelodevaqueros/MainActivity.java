package com.example.duelodevaqueros;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
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

public class MainActivity extends AppCompatActivity {
    private ImageView gunView;
    private Button start_button;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private byte step;
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
     * method to initialize the aplication and presents the start button**/
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
     * method to check if we have permission to use the sensor**/
    private boolean checkActivityRecognitionPermission(){
        return ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * method linked to the principal view, recieves an object of type view and we make the button invisible when it's touched**/
    public void finalCountdown(View startButton){
        startButton.setUiVisibility(View.INVISIBLE);
        checkStepSensor();
    }
}