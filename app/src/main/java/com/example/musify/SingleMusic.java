package com.example.musify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SingleMusic extends AppCompatActivity {

    private ImageView iv;
    private MusicList music;
    private TextView tv;
    private final List<MusicList> musicLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_music);

        iv = findViewById(R.id.iv);
        tv = findViewById(R.id.tv1);


    }



}