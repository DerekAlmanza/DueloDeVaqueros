package com.example.duelodevaqueros;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class SoundPlayer extends JobIntentService {
    public static final String ACTION_FIRE = "com.example.duelodevaqueros.action.FIRE";
    public static final byte JOB_ID = 0;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_FIRE:
                MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.fire);
                soundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(SoundPlayer.class.getSimpleName(),"Freeing sound related resources");
                        mp.release();
                    }
                });
                soundPlayer.start();
                break;
            default:
                Log.d(SoundPlayer.class.getSimpleName(), "Unrecognized action: " +action);
        }
    }
}

