package com.example.musify;

import android.net.Uri;

import java.io.Serializable;

public class MusicList implements Serializable {

    private final  String title,artist,duration;
    private boolean isPlaying;
    private final Uri musicFile;

    public MusicList(String title, String artist, String duration, boolean isPlaying, Uri musicFile) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.musicFile = musicFile;
    }


    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Uri getMusicFile() {
        return musicFile;
    }
}
