package com.example.musify;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.example.musify.CreateNotification.CHANNELID;
import static maes.tech.intentanim.CustomIntent.customType;


public class MainActivity extends AppCompatActivity implements SongChangeListener, PopupMenu.OnMenuItemClickListener, ServiceConnection {

    private final List<MusicList> musicLists = new ArrayList<>();
    private boolean shimmerbool = true;

    private RecyclerView musicRecyclerV;
    private MediaPlayer mediaPlayer;
    private TextView endTime,startTime;
    private boolean isPlaying = false,hide = true;
    private SeekBar playSeekbar;
    private ImageView playpauseImage;
    private Timer timer;
    private int currentSongNumber = 0;
    private MusicAdapter musicAdapater;
    private TextView musicbar_name,musicbar_artist;
    private ImageView menu,theme;
    private long backPresssedTime;
    private Toast backToast;
    private int x = 0;
    static int nextsong = -1;
    static String nextsongTitle = null;
    final Map<String,Integer> mp = new HashMap<>();
    private boolean themecount = true;
    int lightint = 0,darkint = 0;
    ShimmerFrameLayout container;
    NotificationManager notificationManager;

    SharedPreferences pref;

    String currsongtitle = null;

    private LinearLayout bottom_card;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFullScreen();

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
        musicbar_name.setSelected(true);

//        musicbar_artist = findViewById(R.id.musicbar_artist);
        menu = findViewById(R.id.menu);
        bottom_card = findViewById(R.id.bottom_card);
        theme = findViewById(R.id.themes);

        musicRecyclerV.setHasFixedSize(true);
        musicRecyclerV.setLayoutManager(new LinearLayoutManager(this));

        mediaPlayer = new MediaPlayer();



         container = findViewById(R.id.shimmer_view_container);


        mp.clear();
        for(int i = 0; i<musicLists.size(); i++)
        {
            mp.put(musicLists.get(i).getTitle(),i);
        }

        if(shimmerbool)
        {
            shimmer();
            shimmerbool = false;
        }

        // creating a channel
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChannel();
            registerReceiver(broadcastReceiver,new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(),OnClearFromRecentService.class));
        }

//        PhoneStateListener phoneStateListener=new PhoneStateListener()
//        {
//            @Override
//            public void onCallStateChanged(int state, String phoneNumber)
//            {
//                if(state==TelephonyManager.CALL_STATE_RINGING )
//                {
//                    onPaused();
//                }
//                else if(state==TelephonyManager.CALL_STATE_OFFHOOK )
//                {
//                    onPaused();
//                }
//                else if (state==TelephonyManager.CALL_STATE_IDLE)
//                {
//                    if(x!=0)
//                    {
//                        onPlay();
//                    }
//                }
//
//            }
//
//        };
//
//        TelephonyManager manger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        if(manger!= null) {
//            manger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            shimmer();
            getMusicFiles();
        }
        else
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else
            {
                shimmer();
                getMusicFiles();
            }
        }

        menu.setOnClickListener(v -> {

            if(musicLists.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Music not Found!!",Toast.LENGTH_SHORT).show();
                return ;
            }
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.setOnMenuItemClickListener(MainActivity.this);
            popup.inflate(R.menu.menu);
            popup.show();
        });

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        String localTime = date.format(currentLocalTime);

        Log.d(TAG, localTime);

        if(localTime.contains("pm"))
        {
            theme.setImageResource(R.drawable.ic_dark_mode_svgrepo_com);
            if(darkint==0)
            {
                darkint = 1;
                Toast.makeText(getApplicationContext(),"  Nox",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            theme.setImageResource(R.drawable.ic_light_mode_svgrepo_com);
            if(lightint==0)
            {
                lightint = 1;
                Toast.makeText(getApplicationContext(),"Lumos",Toast.LENGTH_SHORT).show();
            }
        }


        playpauseCard.setOnClickListener(v -> {

            if(musicLists.isEmpty())
            {
                bottom_card.setClickable(false);
                return;
            }
            CreateNotification.createNotification(MainActivity.this, musicLists.get(currentSongNumber),R.drawable.ic_baseline_pause_24,currentSongNumber,musicLists.size()-1);

            if(isPlaying)
            {
                onPaused();
            }
            else
            {
                if(x==0)
                {
                    CreateNotification.createNotification(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_pause_24,currentSongNumber,musicLists.size()-1);

                    isPlaying = true;
                    playpauseImage.setImageResource(R.drawable.pause_svg);
                    mediaPlayer.start();
                    musicLists.get(currentSongNumber).setPlaying(true);
                    musicbar_name.setText(musicLists.get(currentSongNumber).getTitle());
                    //        musicbar_artist.setText(musicLists.get(currentSongNumber).getArtist());

                    musicAdapater.update(musicLists);
                    musicRecyclerV.scrollToPosition(currentSongNumber);
                    onChanged(currentSongNumber);
                    x = 1;
                }
                else
                    onPlay();
            }
        });

        nextbtn.setOnClickListener(v -> onNext());

        prevbtn.setOnClickListener(v -> onPrev());


        playSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(musicLists.isEmpty())
                {
                    playSeekbar.setClickable(false);
                    return;
                }

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

    }

    private void shimmer() {

        container.showShimmer(true);
        container.startShimmer();

        new Handler().postDelayed(() -> {
            container.stopShimmer();
            container.hideShimmer();
        }, 2000);

    }


    private void shimmerless() {

        container.showShimmer(true);
        container.startShimmer();

        new Handler().postDelayed(() -> {
            container.stopShimmer();
            container.hideShimmer();
        }, 1000);

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_refresh)
        {
            currsongtitle = musicLists.get(currentSongNumber).getTitle();
            if(nextsong!=-1)
            {
                nextsongTitle = musicLists.get(nextsong).getTitle();nextsong = -1;
            }
            if(mediaPlayer.isPlaying())
            {
                onPaused();
            }

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                shimmer();
                Toast.makeText(getApplicationContext(),"Music fetching...",Toast.LENGTH_SHORT).show();
                getMusicFiles();
            }
            else
            {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else
                {
                    shimmer();
                    Toast.makeText(getApplicationContext(),"Music fetching...",Toast.LENGTH_SHORT).show();
                    getMusicFiles();
                }
            }
            return true;
        }
        else if(id==R.id.action_help)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/vickyjsr/Musify")));
            customType(MainActivity.this,"fadein-to-fadeout");
            return true;
        }
        else if(id==R.id.sorting)
        {
            return true;
        }
        else if(id == R.id.music)
        {
            if(musicLists.isEmpty())
            {
                return true;
            }

            String title = musicLists.get(currentSongNumber).getTitle();

            if(currentSongNumber==nextsong)nextsong = -1;
            if(nextsong!=-1)
            {
                nextsongTitle = musicLists.get(nextsong).getTitle();
            }

            Collections.sort(musicLists, new Comparator<MusicList>() {
                @Override
                public int compare(MusicList o1, MusicList o2) {
                    String x = o1.getTitle(),y = o2.getTitle();

                    x = x.replaceAll(" ","");
                    x = x.toLowerCase();

                    y = y.replaceAll(" ","");
                    y = y.toLowerCase();
                    x = x.replaceAll("[^a-zA-Z0-9]", "");
                    y = y.replaceAll("[^a-zA-Z0-9]", "");

                    return x.compareTo(y);
                }
            });

            shimmerless();

            mp.clear();
            for(int i = 0; i<musicLists.size(); i++)
            {
                mp.put(musicLists.get(i).getTitle(),i);
            }

            if(nextsong!=-1)
            {
                nextsong = mp.get(nextsongTitle);
                nextsongTitle = null;
            }

            musicAdapater = new MusicAdapter(musicLists, MainActivity.this);

            musicRecyclerV.setHasFixedSize(true);
            musicRecyclerV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            musicRecyclerV.setAdapter(musicAdapater);
            currentSongNumber = mp.get(title);
            musicRecyclerV.scrollToPosition(currentSongNumber);
            musicAdapater.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.artist)
        {
            if(musicLists.isEmpty())
            {
                return true;
            }
            String title = musicLists.get(currentSongNumber).getTitle();

            if(currentSongNumber==nextsong)nextsong = -1;
            if(nextsong!=-1)
            {
                nextsongTitle = musicLists.get(nextsong).getTitle();
            }

            Collections.sort(musicLists, new Comparator<MusicList>() {
                @Override
                public int compare(MusicList o1, MusicList o2) {

                    String x = o1.getArtist(),y = o2.getArtist();

                    x = x.replaceAll(" ","");
                    x = x.toLowerCase();

                    y = y.replaceAll(" ","");
                    y = y.toLowerCase();
                    x = x.replaceAll("[^a-zA-Z0-9]", "");
                    y = y.replaceAll("[^a-zA-Z0-9]", "");

                    return x.compareTo(y);
                }
            });

            shimmerless();

            mp.clear();
            for(int i = 0; i<musicLists.size(); i++)
            {
                mp.put(musicLists.get(i).getTitle(),i);
            }

            if(nextsong!=-1)
            {
                nextsong = mp.get(nextsongTitle);
                nextsongTitle = null;
            }

            musicAdapater = new MusicAdapter(musicLists, MainActivity.this);

            musicRecyclerV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            musicRecyclerV.setAdapter(musicAdapater);
            currentSongNumber = mp.get(title);
            musicRecyclerV.scrollToPosition(currentSongNumber);
            musicAdapater.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.duration)
        {
            if(musicLists.isEmpty())
            {
                return true;
            }

            String title = musicLists.get(currentSongNumber).getTitle();
            if(currentSongNumber==nextsong)nextsong = -1;

            if(nextsong!=-1)
            {
                nextsongTitle = musicLists.get(nextsong).getTitle();
            }

            Collections.sort(musicLists, (o1, o2) -> {
                long x = getduration_in_int(o1.getDuration());
                long y = getduration_in_int(o2.getDuration());
                return Long.compare(x,y);
            });

            shimmerless();

            mp.clear();
            for(int i = 0; i<musicLists.size(); i++)
            {
                mp.put(musicLists.get(i).getTitle(),i);
            }

            if(nextsong!=-1)
            {
                nextsong = mp.get(nextsongTitle);
                nextsongTitle = null;
            }

            musicAdapater = new MusicAdapter(musicLists, MainActivity.this);

            musicRecyclerV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            musicRecyclerV.setAdapter(musicAdapater);
            currentSongNumber = mp.get(title);
            musicRecyclerV.scrollToPosition(currentSongNumber);
            musicAdapater.notifyDataSetChanged();
            return true;
        }
        else if(id==R.id.action_contact)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://contact-gourav.netlify.app/")));
            customType(MainActivity.this,"fadein-to-fadeout");
            return true;
        }
        else
            return false;
    }

    private long getduration_in_int(String d) {
        return Long.parseLong(d);
    }

    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void createChannel() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNELID,"Musify",
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
        musicLists.clear();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri,null,MediaStore.Audio.Media.DATA+" Like?",new String[]{"%.mp3"},null);

        //// check this sure

        if(cursor==null)
        {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show());
        }
        else if(!cursor.moveToNext())
        {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "No Music Found!", Toast.LENGTH_SHORT).show();
                playpauseImage.setClickable(false);
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
//                musicbar_artist.setText(getArtistName);
                final MusicList musicList = new MusicList(getmusicfileName,getArtistName,getDuration,false,musicFileuri);

                musicLists.add(musicList);
            }


            musicAdapater = new MusicAdapter(musicLists,MainActivity.this);
            musicRecyclerV.setAdapter(musicAdapater);

            Collections.sort(musicLists, new Comparator<MusicList>() {
                @Override
                public int compare(MusicList o1, MusicList o2) {
                    String x = o1.getTitle(),y = o2.getTitle();

                    x = x.replaceAll(" ","");
                    x = x.toLowerCase();

                    y = y.replaceAll(" ","");
                    y = y.toLowerCase();
                    x = x.replaceAll("[^a-zA-Z0-9]", "");
                    y = y.replaceAll("[^a-zA-Z0-9]", "");

                    return x.compareTo(y);
                }
            });

            for(int i = 0; i<musicLists.size(); i++)
            {
                mp.put(musicLists.get(i).getTitle(),i);
            }


            cursor.close();

            if(musicLists.isEmpty())
            {
                playpauseImage.setClickable(false);
            }

            if(currsongtitle!=null)
                musicbar_name.setText(currsongtitle);
            else
                musicbar_name.setText("Musify");

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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getExtras()).getString("actionName");

            switch (Objects.requireNonNull(action))
            {
                case CreateNotification.ACTION_PREV:
                    onPrev();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()){
                        onPaused();
                    } else {
                        if(isPlaying)
                        {
                            onPaused();
                        }
                        else
                        {
                            onPlay();
                        }
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
            onPaused();
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
        if(!isFinishing())
        {
            finish();
        }
    }


    @Override
    public void onChanged(int position) {

        if(musicLists.isEmpty())
        {
            return ;
        }

        currentSongNumber = position;
        currsongtitle = musicLists.get(currentSongNumber).getTitle();

        CreateNotification.createNotification(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_pause_24,currentSongNumber,musicLists.size()-1);

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        mediaPlayer.stop();
        mediaPlayer.release();

        mediaPlayer = MediaPlayer.create(MainActivity.this, musicLists.get(currentSongNumber).getMusicFile());

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        MainActivity.this.runOnUiThread(() -> {
//                    musicbar_artist.setText(musicLists.get(currentSongNumber).getArtist());
            musicbar_name.setText(musicLists.get(currentSongNumber).getTitle());
        });
        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        mediaPlayer.setOnPreparedListener(mp -> {
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
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    final int getCurrentDuration = mediaPlayer.getCurrentPosition();
                    String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                            TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration)-
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));

                    playSeekbar.setProgress(getCurrentDuration);
                    startTime.setText(generateDuration);

                });

                mediaPlayer.setOnCompletionListener(mp -> {
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

                });
            }
        },1000,1000);

    }



    @Override
    public void onPlay() {

        if(musicLists.isEmpty())
        {
            bottom_card.setClickable(false);
            return;
        }

        if(currsongtitle!=null)
        {
            currentSongNumber = mp.get(currsongtitle);
        }
        else
            currsongtitle = musicLists.get(currentSongNumber).getTitle();

        if(nextsongTitle!=null)
        {
            nextsong = mp.get(nextsongTitle);nextsongTitle = null;
        }

        CreateNotification.createNotification(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_pause_24,currentSongNumber,musicLists.size()-1);

        isPlaying = true;
        playpauseImage.setImageResource(R.drawable.pause_svg);
        mediaPlayer.start();
        musicLists.get(currentSongNumber).setPlaying(true);
        musicbar_name.setText(musicLists.get(currentSongNumber).getTitle());
//        musicbar_artist.setText(musicLists.get(currentSongNumber).getArtist());

        musicAdapater.update(musicLists);
        musicRecyclerV.scrollToPosition(currentSongNumber);
//        onChanged(currentSongNumber);
    }

    @Override
    public void onPrev() {

        if(musicLists.isEmpty())
        {
            bottom_card.setClickable(false);
            return;
        }
        int prevsongnumber = currentSongNumber-1;

        if(prevsongnumber<0)
        {
            prevsongnumber = musicLists.size()-1;
        }

        CreateNotification.createNotification(MainActivity.this,musicLists.get(prevsongnumber),R.drawable.ic_baseline_pause_24,prevsongnumber,musicLists.size()-1);

        musicLists.get(currentSongNumber).setPlaying(false);

        musicLists.get(prevsongnumber).setPlaying(true);

        musicbar_name.setText(musicLists.get(prevsongnumber).getTitle());
//        musicbar_artist.setText(musicLists.get(prevsongnumber).getArtist());

        musicAdapater.update(musicLists);
        musicRecyclerV.scrollToPosition(prevsongnumber);
        onChanged(prevsongnumber);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPaused() {
        currsongtitle = musicLists.get(currentSongNumber).getTitle();

        if(musicLists.isEmpty())
        {
            bottom_card.setClickable(false);
            return;
        }
        CreateNotification.createNotificationPaused(MainActivity.this,musicLists.get(currentSongNumber),R.drawable.ic_baseline_play_arrow_24,currentSongNumber,musicLists.size()-1);

        isPlaying = false;
        mediaPlayer.pause();
        playpauseImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    @Override
    public void onNext() {

        if(musicLists.isEmpty())
        {
            bottom_card.setClickable(false);
            return;
        }

        int nextsongnumber = currentSongNumber+1;
        if(nextsongnumber>=musicLists.size())
        {
            nextsongnumber = 0;
        }

        if(currsongtitle!=null)
        {
            currentSongNumber = mp.get(currsongtitle);
        }
        if(nextsongTitle!=null)
        {
            nextsongnumber = mp.get(nextsongTitle);nextsongTitle = null;
            nextsong = nextsongnumber;
        }
        if(currentSongNumber==nextsong)
        {
            nextsong = -1;
        }

        if(nextsong!=-1)
        {
            nextsongnumber = nextsong;
            nextsong = -1;
        }

        CreateNotification.createNotification(MainActivity.this,musicLists.get(nextsongnumber),R.drawable.ic_baseline_pause_24,nextsongnumber,musicLists.size()-1);


        musicLists.get(currentSongNumber).setPlaying(false);

        musicLists.get(nextsongnumber).setPlaying(true);

        musicbar_name.setText(musicLists.get(nextsongnumber).getTitle());
//        musicbar_artist.setText(musicLists.get(nextsongnumber).getArtist());

        musicAdapater.update(musicLists);
        musicRecyclerV.scrollToPosition(nextsongnumber);
        onChanged(nextsongnumber);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            onPaused();
            unregisterReceiver(broadcastReceiver);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
// The id of the channel.
        String id = "my_channel_id_01";
        notificationManager.deleteNotificationChannel(id);
        finish();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {

        if(backPresssedTime + 2000 > System.currentTimeMillis())
        {
            backToast.cancel();
            super.onBackPressed();
            if (mediaPlayer.isPlaying()) {
                isPlaying = false;
                mediaPlayer.pause();
                mediaPlayer.stop();
                playpauseImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                unregisterReceiver(broadcastReceiver);
            }
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.cancelAll();
            }
// The id of the channel.
            String id = "my_channel_id_01";
            notificationManager.deleteNotificationChannel(id);
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


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
