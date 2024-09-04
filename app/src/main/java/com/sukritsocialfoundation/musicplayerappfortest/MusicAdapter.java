package com.sukritsocialfoundation.musicplayerappfortest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private final List<String> songPaths;
    private final Context context;
    private final OnSongClickListener songClickListener;

    public interface OnSongClickListener {
        void onSongClick(String path);
    }

    public MusicAdapter(List<String> songPaths, Context context, OnSongClickListener songClickListener) {
        this.songPaths = songPaths;
        this.context = context;
        this.songClickListener = songClickListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        String path = songPaths.get(position);
        String title = path.substring(path.lastIndexOf("/") + 1); // Extract the song title from the file path
        holder.songTitle.setText(title);
        holder.itemView.setOnClickListener(v -> songClickListener.onSongClick(path)); // Set click listener
    }

    @Override
    public int getItemCount() {
        return songPaths.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(android.R.id.text1);
        }
    }
}


//import android.content.Context;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.io.File;
//import java.util.List;
//
//public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
//
//    private List<String> songPaths;
//    private Context context;
//    private MediaPlayer mediaPlayer;
//
//    public MusicAdapter(List<String> songPaths, Context context) {
//        this.songPaths = songPaths;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
//        return new MusicViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
//        holder.bind(songPaths.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return songPaths.size();
//    }
//
//    class MusicViewHolder extends RecyclerView.ViewHolder {
//        TextView songTitle;
//
//        MusicViewHolder(View itemView) {
//            super(itemView);
//            songTitle = itemView.findViewById(android.R.id.text1);
//
//            itemView.setOnClickListener(v -> {
//                if (mediaPlayer != null) {
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                }
//                mediaPlayer = MediaPlayer.create(context, Uri.parse(songPaths.get(getAdapterPosition())));
//                mediaPlayer.start();
//            });
//        }
//
//        void bind(String path) {
//            songTitle.setText(new File(path).getName());
//        }
//    }
//}

