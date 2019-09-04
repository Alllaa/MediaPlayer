package com.example.mediaplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import com.example.mediaplayer.model.GetAudios;

public class App extends Application {
    public static final String CHANNEL_ID = "exampleServiceChannel";
    public static final String CLICKED = "click";

    private static MediaPlayer mediaPlayer;

    public static MediaPlayer getInstance() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }
    private static App appClass;

    public static synchronized App getObject() {
        return appClass;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appClass = this;
    }



}
