package com.example.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import static com.example.mediaplayer.AudioService.audios;
import static com.example.mediaplayer.AudioService.notificationLayout;
import static com.example.mediaplayer.AudioService.num;
import static com.example.mediaplayer.view.MainActivity.Duration;
import static com.example.mediaplayer.view.MainActivity.mUpdateTime;
import static com.example.mediaplayer.view.MainActivity.mediaPlayer;
import static com.example.mediaplayer.view.MainActivity.textTime;
import static com.example.mediaplayer.view.MainActivity.time;
import static com.example.mediaplayer.view.MainActivity.tvSongName;
import static com.example.mediaplayer.view.MainActivity.updateUi;

public class ReciverEvents extends BroadcastReceiver {


    static int seekPosition;
    public static Boolean btn = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("Clicked");

        if (action != null) {
            switch (action) {
                case "Close":
                    Log.d("LOG", "CLOSED CLOSED CLOSED");

                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    tvSongName.setText("No song playing! \n");
                    time.setText("00:00");
                    textTime.setText("00:00");

                    updateUi(true,1);
                    Intent inte = new Intent(context, AudioService.class);
                    context.stopService(inte);

                    break;
                case "Pause":
                    Log.d("LOG", "Pause Pause Pause");
                    if (mediaPlayer != null) {


                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            updateUi(true,0);
                            btn = true;
                            Log.d("Hello", "Enter");
                        } else {
                            textTime.post(mUpdateTime);
                            mediaPlayer.start();
                            updateUi(false,0);
                            btn = false;
                            Log.d("Hello", "Enter too");

                        }
                    }
                    break;
                case "Next":
                    Log.d("LOG", "Next Next Next");
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
//                            mediaPlayer = null
                        if (num == audios.size() - 1) {
                            num = 0;
                        } else {
                            num++;
                        }
//                        mediaPlayer = App.getInstance();
                        mediaPlayer = MediaPlayer.create(context, Uri.parse(audios.get(num).getPath()));


                        tvSongName.setText(audios.get(num).getName());
                        time.setText(Duration(mediaPlayer.getDuration()));
                        textTime.post(mUpdateTime);
                        mediaPlayer.start();
                        notificationLayout.setImageViewResource(R.id.stop, R.drawable.stop);
                        Intent in = new Intent(App.getObject(), AudioService.class);
                        in.putExtra("song_name", audios.get(num).getName());
                        Log.d("ELLOS", audios.get(num).getName());
                        in.putExtra("num_of_songs", num);
                        in.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) audios);

                        context.startService(in);

                    }


                    break;
                case "Previous":
                    Log.d("LOG", "Previous Previous Previous");
//                    if (mListener != null) {
//                        mListener.onPreviousClick();
//                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
//                            mediaPlayer = null;


                        if (num == 0) {
                            num = audios.size() - 1;
                        } else {
                            num--;
                        }
                        textTime.post(mUpdateTime);
                        mediaPlayer = MediaPlayer.create(context, Uri.parse(audios.get(num).getPath()));
                        tvSongName.setText(audios.get(num).getName());
                        time.setText(Duration(mediaPlayer.getDuration()));
                        mediaPlayer.start();
                        notificationLayout.setImageViewResource(R.id.stop, R.drawable.stop);
                        Intent in = new Intent(App.getObject(), AudioService.class);
                        in.putExtra("song_name", audios.get(num).getName());
                        Log.d("ELLOS", audios.get(num).getName());
                        in.putExtra("num_of_songs", num);
                        in.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) audios);

                        context.startService(in);

                    }
                    break;
            }
        }

    }
}
