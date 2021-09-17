package com.example.musify;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import soup.neumorphism.NeumorphCardView;

import static android.content.ContentValues.TAG;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> implements View.OnClickListener {

    private List<MusicList> list;
    private final Context context;
    private int playingPosition = 0;
    private  final SongChangeListener songChangeListener;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener)context);
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter,null));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MusicList list2 = list.get(position);

        if(list2.isPlaying())
        {
            playingPosition = position;
            holder.rootlayout.setBackgroundResource(R.drawable.roundglass);
        }
        else
        {
            holder.rootlayout.setBackgroundResource(R.drawable.round_back_blue_10);
        }

        String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(list2.getDuration()))-
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration()))));
        holder.title.setText(list2.getTitle());
        holder.artist.setText(list2.getArtist());
        holder.musicDuration.setText(generateDuration);

        holder.rootlayout.setOnClickListener(v -> {
            list.get(playingPosition).setPlaying(false);
            list2.setPlaying(true);
            songChangeListener.onChanged(position);
            notifyDataSetChanged();
        });

        holder.iv.setOnClickListener(view -> {

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, holder.iv);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_adapter);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                int action = item.getItemId();
                if(action==R.id.action_delete)
                {
                    removeAt(position,view);
                    return true;
                }
                else if(action==R.id.play_next)
                {
                    MainActivity.nextsong = position;
                    return true;
                }
                else return false;
            });
            //displaying the popup
            popup.show();

        });

    }


    public void removeAt(int position,View v) {
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setMessage("Are you sure!!").setCancelable(false).setPositiveButton("YES", (dialog, which) -> {
//
//            Uri uri = list.get(position).getMusicFile();
//
//            ContentResolver contentResolver = context.getContentResolver();
//            contentResolver.delete (uri,null ,null );
////            MainActivity.musicLists.remove(position);
//            list.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position,list.size());
            Toast.makeText(context,"Not Implemented yet!!",Toast.LENGTH_SHORT).show();

        }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    public void update(List<MusicList> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onClick(View v) {

    }



    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final RelativeLayout rootlayout;
        private final TextView title,artist;
        private final TextView musicDuration;
        private final ImageButton iv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rootlayout = itemView.findViewById(R.id.rootlayout);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
            iv = itemView.findViewById(R.id.showOptions);

        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.play_next,
                    Menu.NONE, R.string.edit);
            menu.add(Menu.NONE, R.id.action_delete,
                    Menu.NONE, R.string.delete);
        }

    }
}
