package com.example.mediaplayer.viewmodel;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mediaplayer.model.Audio;
import com.example.mediaplayer.model.GetAudios;

import java.util.List;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Audio>> liveData;
    private GetAudios getAudios = GetAudios.getInstance();

    public LiveData<List<Audio>> getAudios(Cursor cursor)
    {
        liveData = getAudios.getAllAudioFromDevice(cursor);
        return liveData;
    }
}
