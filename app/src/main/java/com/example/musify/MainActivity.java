package com.example.musify;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

// bundle or putExtra

public class MainActivity extends AppCompatActivity implements SongChangeListener{

    private final List<MusicList> musicLists = new ArrayList<>();

    private RecyclerView musicRecyclerV;
    private MediaPlayer mediaPlayer;
    private TextView endTime,startTime;
    private boolean isPlaying = false;
    private SeekBar playSeekbar;
    private ImageView playpauseImage;
    private Timer timer;
    private int currentSongNumber = 0;
    private MusicAdapter musicAdapater;
    private TextView musicbar_name,musicbar_artist;
    private ImageView menu,info;
    private long backPresssedTime;
    private Toast backToast;


    NotificationManager notificationManager;

    private LinearLayout bottom_card;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decodeView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decodeView.setSystemUiVisibility(options);

        setContentView(R.layout.activity_main);

        final LinearLayout menubtn = findViewById(R.id.menu_bar);
        musicRecyclerV = findViewById(R.id.music_recycler_view);
        final CardView playpauseCard = findViewById(R.id.play_pause_card);
        playpauseImage = findViewById(R.id.play_pause_button);
        final ImageView nextbtn = findViewById(R.id.next);
        final ImageView prevbtn = findViewById(R.id.prev);

        playSeekbar = findViewById(R.id.playerseekbar);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        musicbar_name = findViewById(R.id.musicbar_name);
        musicbar_artist = findViewById(R.id.musicbar_artist);
        menu = findViewById(R.id.menu);
        bottom_card = findViewById(R.id.bottom_card);
        info = findViewById(R.id.info);

        musicRecyclerV.setHasFixedSize(true);
        musicRecyclerV.setLayoutManager(new LinearLayoutManager(this));

        mediaPlayer = new MediaPlayer();


        // creating a channel
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChannel();
            registerReceiver(broadcastReceiver,new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(),OnClearFromRecentService.class));

        }



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            getMusicFiles();
        }
        else
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},11);
            }
            else
            {
                getMusicFiles();
            }
        }

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://q4gaha7wghudomzmwqcv6w-on.drv.tw/Profile/profile1.html")));
            }
        });

        playpauseCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CreateNotification.createNotification(MainActivity.this, musicLists.get(1),R.drawable.ic_baseline_pause_24,1,musicLists.size()-1);

                if(isPlaying)
                {
                    onPaused();
                }
                else
                {
                    onPlay();
                }
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrev();
            }
        });


        playSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    if(isPlaying)
                    {
                        mediaPlayer.seekTo(progress);
                    }
                    else
                    {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bottom_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {

                }

            }
        });
    }




    private void createChannel() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CreateNotification.CHANNELID,"Musify",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager!=null)
            {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void getMusicFiles()
    {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri,null,MediaStore.Audio.Media.DATA+" Like?",new String[]{"%.mp3"},null);


        //// check this sure

        if(cursor==null)
        {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(!cursor.moveToNext())
        {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "No Music Found!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            while(cursor.moveToNext())
            {
                final String getmusicfileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                final String getArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long cursorid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                Uri musicFileuri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cursorid);
                
                String getDuration = "00:00";
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
                {
                    getDuration = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));
                }
                musicbar_name.setText(getmusicfileName);
                musicbar_artist.setText(getArtistName);
                final MusicList musicList = new MusicList(getmusicfileName,getArtistName,getDuration,false,musicFileuri);
                musicLists.add(musicList);
            }


            musicAdapater = new MusicAdapter(musicLists,MainActivity.this);
            musicRecyclerV.setAdapter(musicAdapater);
            cursor.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getMusicFiles();
        else Toast.makeText(this, "Permission Declined", Toast.LENGTH_SHORT).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus)
        {
            View decodeView = getWindow().getDecorView();
            int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decodeView.setSystemUiVisibility(options);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");

            switch (action)
            {
                case CreateNotification.ACTION_PREV:
                    onPrev();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying){
                        onPaused();
                    } else {
                        onPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onNext();
                    break;
                case CreateNotification.ACTION_CLOSE:
                    closeNoti();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + action);
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void closeNoti() {
        if(mediaPlayer.isPlaying())
        {
            unregisterReceiver(broadcastReceiver);
        }
        onPaused();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notificationManager.cancelAll();
        }
// The id of the channel.
        String id = "my_channel_id_01";
        notificationManager.deleteNotificationChannel(id);
        finish();
    }


    @Override
    public void onChanged(int position) {
        currentSongNumber = position;
        CreateNotification.createNotification(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_pause_24,currentSongNumber,musicLists.size()-1);

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(),musicLists.get(currentSongNumber).getMusicFile());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicbar_artist.setText(musicLists.get(currentSongNumber).getArtist());
                            musicbar_name.setText(musicLists.get(currentSongNumber).getTitle());
                        }
                    });

                    try {
                        mediaPlayer.prepare();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                } catch (IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"Unable to Play Track!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final int getTotalDuration = mp.getDuration();


                String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(getTotalDuration)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));
                endTime.setText(generateDuration);
                isPlaying = true;
                mp.start();

                playSeekbar.setMax(getTotalDuration);
                playpauseImage.setImageResource(R.drawable.pause_svg);
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int getCurrentDuration = mediaPlayer.getCurrentPosition();
                        String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                                TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration)-
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));

                        playSeekbar.setProgress(getCurrentDuration);
                        startTime.setText(generateDuration);

                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.reset();
                        String tm = "00:00";
                        startTime.setText(tm);
                        timer.purge();
                        timer.cancel();

                        isPlaying = false;
                        playpauseImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        playSeekbar.setProgress(0);

                        musicAdapater.update(musicLists);
                        musicRecyclerV.scrollToPosition(0);

                        // starts next music
                        onNext();

                    }
                });
            }
        },1000,1000);

    }


    @Override
    public void onPlay() {

        CreateNotification.createNotification(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_pause_24,currentSongNumber,musicLists.size()-1);

        isPlaying = true;
        mediaPlayer.start();
        playpauseImage.setImageResource(R.drawable.pause_svg);
        musicLists.get(currentSongNumber).setPlaying(true);
        musicbar_name.setText(musicLists.get(currentSongNumber).getTitle());
        musicbar_artist.setText(musicLists.get(currentSongNumber).getArtist());

        musicAdapater.update(musicLists);
        musicRecyclerV.scrollToPosition(currentSongNumber);
        onChanged(currentSongNumber);

    }

    @Override
    public void onPrev() {

        int prevsongnumber = currentSongNumber-1;

        if(prevsongnumber<0)
        {
            prevsongnumber = musicLists.size()-1;
        }

        CreateNotification.createNotification(MainActivity.this,musicLists.get(prevsongnumber),R.drawable.ic_baseline_pause_24,prevsongnumber,musicLists.size()-1);

        musicLists.get(currentSongNumber).setPlaying(false);
        musicLists.get(prevsongnumber).setPlaying(true);
        musicbar_name.setText(musicLists.get(prevsongnumber).getTitle());
        musicbar_artist.setText(musicLists.get(prevsongnumber).getArtist());

        musicAdapater.update(musicLists);
        musicRecyclerV.scrollToPosition(prevsongnumber);
        onChanged(prevsongnumber);
    }

    @Override
    public void onPaused() {

        CreateNotification.createNotification(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_play_arrow_24,currentSongNumber,musicLists.size()-1);

        isPlaying = false;
        mediaPlayer.pause();
        playpauseImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    @Override
    public void onNext() {

        int nextsongnumber = currentSongNumber+1;
        if(nextsongnumber==musicLists.size())
        {
            nextsongnumber = 0;
        }


        CreateNotification.createNotification(MainActivity.this,musicLists.get(nextsongnumber),R.drawable.ic_baseline_pause_24,nextsongnumber,musicLists.size()-1);


        musicLists.get(currentSongNumber).setPlaying(false);
        musicLists.get(nextsongnumber).setPlaying(true);

        musicbar_name.setText(musicLists.get(nextsongnumber).getTitle());
        musicbar_artist.setText(musicLists.get(nextsongnumber).getArtist());

        musicAdapater.update(musicLists);
        musicRecyclerV.scrollToPosition(nextsongnumber);
        onChanged(nextsongnumber);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.isPlaying())
        {
            unregisterReceiver(broadcastReceiver);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notificationManager.cancelAll();
        }
// The id of the channel.
        String id = "my_channel_id_01";
        notificationManager.deleteNotificationChannel(id);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {

        if(backPresssedTime + 2000 > System.currentTimeMillis())
        {
            backToast.cancel();
            isPlaying = false;
            mediaPlayer.pause();
            playpauseImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);

            super.onBackPressed();
            finish();
            return;
        }
        else
        {
            backToast= Toast.makeText(getApplicationContext(),"Press Back Again to Exit!!",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPresssedTime = System.currentTimeMillis();

    }

}
