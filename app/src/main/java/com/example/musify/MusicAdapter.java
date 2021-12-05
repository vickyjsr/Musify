package com.example.musify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musify.data.MyDbHandler;
import com.example.musify.model.UriStore;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> implements View.OnClickListener, Serializable {

    public List<MusicList> list;
    private final Context context;
    private int playingPosition = 0;
    private  final SongChangeListener songChangeListener;
    MyDbHandler db;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener)context);
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = new MyDbHandler(context);
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        boolean first = prefs.getBoolean("firstStart",true);
        if(!first)
        {
            createTableOnFirstStart();
        }
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter,parent,false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int p) {

        int position = holder.getAbsoluteAdapterPosition();
        MusicList list2 = list.get(position);

        readCursorData(list2,holder);

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
                if(action==R.id.play_next)
                {
                    MainActivity.nextsong = position;
                    return true;
                }
                else if(action==R.id.action_info)
                {
                    songChangeListener.showBottomSheetDialog();
                    return true;
                }
                else return false;
            });
            //displaying the popup
            popup.show();

        });

    }



    @SuppressLint("NotifyDataSetChanged")
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



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,Serializable {

        private final RelativeLayout rootlayout;
        private final TextView title,artist;
        private final TextView musicDuration;
        private final ImageButton iv;
        private final ImageView like_dislike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rootlayout = itemView.findViewById(R.id.rootlayout);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
            iv = itemView.findViewById(R.id.showOptions);
            like_dislike = itemView.findViewById(R.id.like_dislike);

            artist.setHorizontallyScrolling(true);
            artist.setSelected(true);

            like_dislike.setOnClickListener(v -> {
                int posi = getAbsoluteAdapterPosition();
                MusicList mlist = list.get(posi);
                if(mlist.isFav().equals("0"))
                {
                    mlist.setFav("1");
                    UriStore uris = new UriStore(mlist.getTitle(),mlist.getMusicFile().toString());
                    db.addUri(uris);
                    like_dislike.setBackgroundResource(R.drawable.ic_heart_like);
                }
                else
                {
                    mlist.setFav("0");
                    db.removeUri(mlist.getTitle());
                    like_dislike.setBackgroundResource(R.drawable.ic_heart_dislike);
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.play_next,
                    Menu.NONE, "Play Next");
            menu.add(Menu.NONE, R.id.action_info,
                    Menu.NONE, "Song info");
        }
    }


    private void createTableOnFirstStart() {
        db.insertEmpty();
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart",false);
        editor.apply();
    }

    private void readCursorData(MusicList list2, MyViewHolder viewHolder) {
        String name = list2.getTitle();
        name = name.replaceAll(" ","");
        name = name.replaceAll("[^a-zA-Z0-9]", "");
        name = name.toLowerCase();

        List<UriStore> favList = db.select_all_fav_list();

        String item_fav_status = "0";

        for(int i=0;i<favList.size();i++)
        {
            if(favList.get(i).getMname().equals(name))
            {
                item_fav_status = "1";
            }
        }

        list2.setFav(item_fav_status);
        if (item_fav_status.equals("1")) {
            viewHolder.like_dislike.setBackgroundResource(R.drawable.ic_heart_like);
        } else {
            viewHolder.like_dislike.setBackgroundResource(R.drawable.ic_heart_dislike);
        }
    }

}
