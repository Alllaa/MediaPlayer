package com.example.mediaplayer.model;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class GetAudios {

    private static GetAudios getAudios;
    private MutableLiveData<List<Audio>>audioList = new MutableLiveData<>();
    private List<Audio>dataSet = new ArrayList<>();
    public static GetAudios getInstance() {
        if (getAudios == null) {
            getAudios = new GetAudios();
        }
        return getAudios;
    }


    public MutableLiveData<List<Audio>> getAllAudioFromDevice(Cursor cursor) {
        if (isSdPresent()) {
            int i = 0;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Audio audio  = new Audio();
                        audio.setName(cursor
                                .getString(cursor
                                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                        audio.setId(cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Audio.Media._ID)));

                        audio.setPath(cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA)));

                        audio.setNumOfSong(i);
                        dataSet.add(audio);
                        i++;

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        audioList.postValue(dataSet);
        return audioList;
    }


    public static boolean isSdPresent()
    {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}
