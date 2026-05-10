package com.example.appmusica.song;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmusica.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    private List<Song> songs;
    private final OnSongClickListener listener;

    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs != null ? songs : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<Song> newSongs) {
        this.songs = newSongs != null ? newSongs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (songs == null || position < 0 || position >= songs.size()) {
            return;
        }

        Song song = songs.get(position);

        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.tvMeta.setText(String.format(Locale.getDefault(), "%s   %d", song.getDuration(), song.getYear()));
        holder.tvGenre.setText(song.getGenre());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", song.getRating()));
        holder.tvRatingCount.setText(song.getRatingCount());

        double rating = song.getRating();
        if (rating <= 2.0) {
            holder.tvRating.setTextColor(Color.parseColor("#FF4C4C"));
        } else if (rating <= 3.5) {
            holder.tvRating.setTextColor(Color.parseColor("#FFD966"));
        } else {
            holder.tvRating.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist, tvMeta, tvGenre, tvRating, tvRatingCount;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvRatingCount = itemView.findViewById(R.id.tvRatingCount);
        }
    }
}