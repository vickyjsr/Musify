package com.example.musify;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

public class CreateNotification  {
    public static final String CHANNELID = "my_channel_id_01";
    public static final String ACTION_PREV = "Prev";
    public static final String ACTION_PLAY = "Play";
    public static final String ACTION_PAUSED = "Pause";
    public static final String ACTION_NEXT = "Next";
    public static final String ACTION_CLOSE = "close";

    public static Notification notification;

    public static void createNotification(Context context,MusicList musicList,int playButton,int pos,int size)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context,"tag");

            PendingIntent pendingIntentPrev;
            int drw_prev;
            int close = R.drawable.ic_baseline_close_24;

            if(pos==0)
            {
                pendingIntentPrev = null;
                drw_prev = 0;
            }
            else
            {
                Intent intentPrev = new Intent(context,NotificationActionService.class)
                        .setAction(ACTION_PREV);
                pendingIntentPrev = PendingIntent.getBroadcast(context,
                        0, intentPrev,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_prev = R.drawable.ic_baseline_skip_previous_24;
            }


            Intent intentPlay = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context,
                    1, intentPlay,PendingIntent.FLAG_CANCEL_CURRENT);

            PendingIntent pendingIntentNext;
            int drw_next = 0;

            if(pos==size)
            {
                pendingIntentNext = null;
                drw_next = 0;
            }
            else
            {
                Intent intentNext = new Intent(context,NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context,
                        2, intentNext,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_baseline_skip_next_24;
            }


            Intent intentClose = new Intent(context,NotificationActionService.class)
                    .setAction(ACTION_CLOSE);
            PendingIntent pendingIntentclose = PendingIntent.getBroadcast(context,
                    3, intentClose,PendingIntent.FLAG_CANCEL_CURRENT);


            notification = new NotificationCompat.Builder(context,CHANNELID)
                    .setSmallIcon(R.drawable.ic_logo3)
                    .setContentTitle(musicList.getTitle())
                    .setContentText(musicList.getArtist())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .addAction(drw_prev,"Previous",pendingIntentPrev)
                    .addAction(playButton,"Play",pendingIntentPlay)
                    .addAction(drw_next,"Next",pendingIntentNext)
                    .addAction(close,"close",pendingIntentclose)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2,3)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

            notificationManagerCompat.notify(1,notification);



        }
    }

}