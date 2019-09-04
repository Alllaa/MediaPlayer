package com.example.mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.mediaplayer.model.Audio;
import com.example.mediaplayer.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.mediaplayer.App.CHANNEL_ID;
import static com.example.mediaplayer.App.CLICKED;


public class AudioService extends Service {

    static List<Audio> audios = new ArrayList<>();
    public static RemoteViews notificationLayout;
    public static int num;
   public static NotificationManager manager;
    public static Notification notification;
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String songName = intent.getStringExtra("song_name");
        num = intent.getIntExtra("num_of_songs", 0);
        if (audios.size() == 0)
            audios.addAll(intent.<Audio>getParcelableArrayListExtra("songs"));

        notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_layout_notification);
        notificationLayout.setTextViewText(R.id.song_name, songName);

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1, notificationIntent, 0);

        Intent previous = new Intent(this, ReciverEvents.class);
        previous.putExtra("Clicked", "Previous");
        previous.setAction(CLICKED);
        PendingIntent pPreviousIntent = PendingIntent.getBroadcast(this, 2,
                previous, 0);

        Intent next = new Intent(this, ReciverEvents.class);
        next.putExtra("Clicked", "Next");
        next.setAction(CLICKED);
        PendingIntent pNextIntent = PendingIntent.getBroadcast(this, 3,
                next, 0);

        Intent pause = new Intent(this, ReciverEvents.class);
        pause.putExtra("Clicked", "Pause");
        pause.setAction(CLICKED);
        PendingIntent pPauseIntent = PendingIntent.getBroadcast(this, 4,
                pause, 0);

        Intent close = new Intent(this, ReciverEvents.class);
        close.putExtra("Clicked", "Close");
        close.setAction(CLICKED);
        PendingIntent pClose = PendingIntent.getBroadcast(this, 5,
                close, 0);

        notificationLayout.setOnClickPendingIntent(R.id.close, pClose);
        notificationLayout.setOnClickPendingIntent(R.id.previous, pPreviousIntent);
        notificationLayout.setOnClickPendingIntent(R.id.next, pNextIntent);
        notificationLayout.setOnClickPendingIntent(R.id.stop, pPauseIntent);


        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.music)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
//                .setContentIntent(pPreviousIntent)
//                .setContentIntent(pNextIntent)
//                .setContentIntent(pPauseIntent)
//                .setContentIntent(pClose)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
