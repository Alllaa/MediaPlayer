package com.example.mediaplayer.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.App;
import com.example.mediaplayer.AudioService;
import com.example.mediaplayer.R;
import com.example.mediaplayer.adapter.Adapter;
import com.example.mediaplayer.model.Audio;
import com.example.mediaplayer.viewmodel.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mediaplayer.AudioService.manager;
import static com.example.mediaplayer.AudioService.notification;
import static com.example.mediaplayer.AudioService.notificationLayout;
import static com.example.mediaplayer.AudioService.num;


public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    List<Audio> audioList = new ArrayList<Audio>();
    RecyclerView recyclerView;
    Adapter adapter;
    private String[] STAR = {"*"};
    Cursor cursor;
    Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    public static MediaPlayer mediaPlayer;
    public MainViewModel mainViewModel;
    public static Intent intent;
    private ImageView ivNext, ivPrevious;
    private static ImageView ivPause;
    private static SeekBar mSeekBar, vSeekBar;
    int seekPostion;
    private BottomSheetBehavior mBottomSheetBehavior;
    LinearLayout line1, line2, line5;
    public static TextView tvSongName, time;
    ImageView imageView;
    AudioManager audioManager;
    public static TextView textTime;
    SettingsContentObserver mSettingsContentObserver;
    RelativeLayout relativeLayout;
    public static int numOfSong;
    int count = 0;
    static Thread t;

    @Override
    protected void onStart() {
        super.onStart();
        mSettingsContentObserver = new SettingsContentObserver(new Handler());
        this.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textTime = findViewById(R.id.timer);
        relativeLayout = findViewById(R.id.line6);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        intent = new Intent(this, AudioService.class);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        ivNext = findViewById(R.id.next_main);
        ivPause = findViewById(R.id.stop_main);
        ivPrevious = findViewById(R.id.previous_main);
        mSeekBar = findViewById(R.id.seek);
        vSeekBar = findViewById(R.id.seekBar_volume);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        vSeekBar.setMax(maxVolume);
        vSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line5 = findViewById(R.id.line5);
        tvSongName = findViewById(R.id.song_name);
        final View bottonSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottonSheet);
        mBottomSheetBehavior.setHideable(false);
        imageView = findViewById(R.id.img);
        time = findViewById(R.id.time);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        line1.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.GONE);

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        line2.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        line1.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            cursor = getContentResolver().query(allsongsuri, STAR, selection, null, null);
            adapter = new Adapter(this, audioList);
            recyclerView.setAdapter(adapter);
            mainViewModel.getAudios(cursor).observe(this, new Observer<List<Audio>>() {
                @Override
                public void onChanged(List<Audio> audio) {
                    audioList.clear();
                    audioList.addAll(audio);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        adapter.setOnSongListener(new Adapter.OnItemClickListener() {
            @Override
            public void onSongplay(int position) {
                Toast.makeText(MainActivity.this, audioList.get(position).getName(), Toast.LENGTH_SHORT).show();
                // initialize Uri here
                stopPlaying();
                stopService(intent);
                mediaPlayer = App.getInstance();
//
                mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(audioList.get(position).getPath()));
                mSeekBar.setMax(mediaPlayer.getDuration() / 1000);
                tvSongName.setText(audioList.get(position).getName());
                Log.d("HATTTTIME", mediaPlayer.getDuration() + "");
                time.setText(Duration(mediaPlayer.getDuration()));
                textTime.post(mUpdateTime);
                mediaPlayer.start();

                ivPause.setImageResource(R.drawable.stop);
                intent.putExtra("song_name", audioList.get(position).getName());
                numOfSong = audioList.get(position).getNumOfSong();
                intent.putExtra("num_of_songs", audioList.get(position).getNumOfSong());
                intent.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) audioList);

                startService(intent);
                ContextCompat.startForegroundService(MainActivity.this, intent);

            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    try {
                        mSeekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);

                    } catch (Exception ignored) {

                    }

                }
            }
        }, 0, 500);


        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();

                    seekPostion = mediaPlayer.getCurrentPosition();
                    updateUi(mediaPlayer.isPlaying(), 0);
                    ivPause.setImageResource(R.drawable.play);
                } else {
                    if (mediaPlayer != null) {
                        updateUi(!mediaPlayer.isPlaying(), 0);
                        mSeekBar.setMax(mediaPlayer.getDuration() / 1000);
                        textTime.post(mUpdateTime);
                        mediaPlayer.start();
                        ivPause.setImageResource(R.drawable.stop);
                    }
                }
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    stopPlaying();
                    if (num == audioList.size() - 1) {
                        num = 0;
                    } else {
                        num++;
                    }
                    mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(audioList.get(num).getPath()));
                    tvSongName.setText(audioList.get(num).getName());

                    time.setText(Duration(mediaPlayer.getDuration()));
                    textTime.post(mUpdateTime);
                    mediaPlayer.start();
                    mSeekBar.setMax(mediaPlayer.getDuration() / 1000);
                    ivPause.setImageResource(R.drawable.stop);
                    Intent intentNext = new Intent(MainActivity.this, AudioService.class);
                    intentNext.putExtra("song_name", audioList.get(num).getName());
                    intentNext.putExtra("num_of_songs", num);
                    intentNext.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) audioList);
                    startService(intentNext);

                }


            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    stopPlaying();
                    if (num == 0) {
                        num = audioList.size() - 1;
                    } else {
                        num--;
                    }

                    mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(audioList.get(num).getPath()));
                    tvSongName.setText(audioList.get(num).getName());

                    time.setText(Duration(mediaPlayer.getDuration()));
                    textTime.post(mUpdateTime);
                    mediaPlayer.start();
                    ivPause.setImageResource(R.drawable.stop);
                    Intent intentPrev = new Intent(MainActivity.this, AudioService.class);
                    intentPrev.putExtra("song_name", audioList.get(num).getName());
                    intentPrev.putExtra("num_of_songs", num);
                    intentPrev.putParcelableArrayListExtra("songs", (ArrayList<? extends Parcelable>) audioList);
                    startService(intentPrev);

                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b && mediaPlayer != null) {
                    mSeekBar.setMax(mediaPlayer.getDuration() / 1000);
                    mediaPlayer.seekTo(i * 1000);


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    public static void updateUi(boolean bool, int x) {
        if (bool) {
            ivPause.setImageResource(R.drawable.play);
            notificationLayout.setImageViewResource(R.id.stop, R.drawable.play);
            manager.notify(1, notification);
        } else {
            ivPause.setImageResource(R.drawable.stop);
            notificationLayout.setImageViewResource(R.id.stop, R.drawable.stop);
            manager.notify(1, notification);
        }
        if (x == 1) {
            mSeekBar.setProgress(0);
        }
    }

    public static String Duration(int time) {
        int minutes = ((time % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (((time % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        String d;
        if (seconds > 0 && seconds < 10) {
            d = "0" + minutes + ":0" + seconds;

        } else
            d = "0" + minutes + ":" + seconds;


        Log.d("TIME", d);
        return d;
    }



    public class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v("ASDR", "Settings change detected");
            vSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);

    }

    public static Runnable mUpdateTime = new Runnable() {
        public void run() {
            int currentDuration;
            if (mediaPlayer !=null )
            {


            if (mediaPlayer.isPlaying()) {
                currentDuration = mediaPlayer.getCurrentPosition();
                updatePlayer(currentDuration);
                textTime.postDelayed(this, 1000);
            }else {
                textTime.removeCallbacks(this);
            }
            }
        }
    };

    public static void updatePlayer(int currentDuration){
        textTime.setText("" + milliSecondsToTimer((long) currentDuration));
    }

    /**
     * Function to convert milliseconds time to Timer Format
     * Hours:Minutes:Seconds
     * */
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


}
