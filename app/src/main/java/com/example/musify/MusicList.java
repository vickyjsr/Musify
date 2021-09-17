package com.example.musify;

import android.net.Uri;

import java.io.Serializable;

public class MusicList implements Serializable {

    private final  String title,artist,duration;
    private boolean isPlaying;
    private final Uri musicFile;
    private String id, path;
    public MusicList(String title, String artist, String duration, boolean isPlaying, Uri musicFile, long cursorid, String id, String path) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.musicFile = musicFile;
        this.id = id;
        this.path = path;
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

    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
