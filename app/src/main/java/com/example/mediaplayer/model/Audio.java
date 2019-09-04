package com.example.mediaplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Audio implements Parcelable {
    private String path;
    private String name;
    private int id;
    private int numOfSong;

    public Audio()
    {
//        aPath = "";
//        aName = "";
//        id = 0;
//        numOfSong = 0;
    }
    public Audio(Parcel in)
    {
        this.path = in.readString();
        this.name = in.readString();
        this.id = in.readInt();
        this.numOfSong = in.readInt();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public int getNumOfSong() {
        return numOfSong;
    }

    public void setNumOfSong(int numOfSong) {
        this.numOfSong = numOfSong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeInt(numOfSong);

    }
}
