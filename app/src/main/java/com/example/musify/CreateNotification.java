package com.example.musify;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;

import java.io.IOException;

public class CreateNotification  extends Service {
    public static final String CHANNELID = "my_channel_id_01";
    public static final String ACTION_PREV = "Prev";
    public static final String ACTION_PLAY = "Play";
    public static final String ACTION_PAUSED = "Pause";
    public static final String ACTION_NEXT = "Next";
    public static final String ACTION_CLOSE = "close";
    private MediaSession mediaSession;
    private MediaPlayer mediaPlayer;
    private static MediaSessionCompat mediaSessionCompat;
    private MediaSessionManager mediaSessionManager;
    private MediaController mediaController;

    public static Notification notification;



    public static void createNotification(Context context, MusicList musicList, int playButton, int pos, int size)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            mediaSessionCompat = new MediaSessionCompat(context,"tag");

            PendingIntent pendingIntentPrev;
            int drw_prev;
            int close = R.drawable.ic_block_svgrepo_com;

            Intent intentPrev = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_PREV);
            pendingIntentPrev = PendingIntent.getBroadcast(context,
                    1, intentPrev,PendingIntent.FLAG_UPDATE_CURRENT);
            drw_prev = R.drawable.ic_baseline_skip_previous_24;


            Intent intentPlay = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context,
                    1, intentPlay,PendingIntent.FLAG_UPDATE_CURRENT );

            PendingIntent pendingIntentNext;
            int drw_next = R.drawable.ic_baseline_skip_next_24;
            Intent intentNext = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context,
                    1, intentNext,PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentClose = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_CLOSE);
            PendingIntent pendingIntentclose = PendingIntent.getBroadcast(context,
                    1, intentClose,PendingIntent.FLAG_UPDATE_CURRENT );

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_foreground);
            BitmapFactory.Options bfo=new BitmapFactory.Options();

            mmr.setDataSource(context, musicList.getMusicFile());
            rawArt = mmr.getEmbeddedPicture();

// if rawArt is null then no cover art is embedded in the file or is not
// recognized as such.
            if (null != rawArt)
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);

            notification = new NotificationCompat.Builder(context,CHANNELID)
                    .setSmallIcon(R.drawable.ic_logo3)
                    .setContentTitle(musicList.getTitle())
                    .setContentText(musicList.getArtist())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .addAction(drw_prev,"Previous",pendingIntentPrev)
                    .addAction(playButton,"Play",pendingIntentPlay)
                    .addAction(drw_next,"Next",pendingIntentNext)
                    .addAction(close,"close",pendingIntentclose)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2,3)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setColor(Color.argb(1,218, 223, 225))
                    .setLargeIcon(art)
                    .build();

            notification.flags |= Notification.FLAG_ONGOING_EVENT;

            notificationManagerCompat.notify(1,notification);



        }
    }

    public static void createNotificationPaused(Context context,MusicList musicList,int playButton,int pos,int size)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            mediaSessionCompat = new MediaSessionCompat(context,"tag");

            PendingIntent pendingIntentPrev;
            int drw_prev;
            int close = R.drawable.ic_block_svgrepo_com;

            Intent intentPrev = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_PREV);
            pendingIntentPrev = PendingIntent.getBroadcast(context,
                    1, intentPrev,PendingIntent.FLAG_UPDATE_CURRENT);
            drw_prev = R.drawable.ic_baseline_skip_previous_24;


            Intent intentPlay = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context,
                    1, intentPlay,PendingIntent.FLAG_UPDATE_CURRENT );

            PendingIntent pendingIntentNext;
            int drw_next = R.drawable.ic_baseline_skip_next_24;
            Intent intentNext = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context,
                    1, intentNext,PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentClose = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_CLOSE);
            PendingIntent pendingIntentclose = PendingIntent.getBroadcast(context,
                    1, intentClose,PendingIntent.FLAG_UPDATE_CURRENT );


            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_foreground);
            BitmapFactory.Options bfo=new BitmapFactory.Options();

            mmr.setDataSource(context, musicList.getMusicFile());
            rawArt = mmr.getEmbeddedPicture();

// if rawArt is null then no cover art is embedded in the file or is not
// recognized as such.
            if (null != rawArt)
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);

            notification = new NotificationCompat.Builder(context,CHANNELID)
                    .setSmallIcon(R.drawable.ic_logo3)
                    .setContentTitle(musicList.getTitle())
                    .setContentText(musicList.getArtist())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setOngoing(false)
                    .setAutoCancel(false)
                    .addAction(drw_prev,"Previous",pendingIntentPrev)
                    .addAction(playButton,"Play",pendingIntentPlay)
                    .addAction(drw_next,"Next",pendingIntentNext)
                    .addAction(close,"close",pendingIntentclose)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2,3)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setColor(Color.argb(1,218, 223, 225))
                    .setLargeIcon(art)
                    .build();

            notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            notificationManagerCompat.notify(1,notification);


        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}