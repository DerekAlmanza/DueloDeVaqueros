package com.example.duelodevaqueros;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private ExecutorService singleThreadProducer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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

    /*
    * Occupies a handler to execute after 3000 milliseconds (3 seconds)
    * to restart the duel.
    * */
    public void fire(View gun) {
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
