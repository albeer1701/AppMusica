package com.example.appmusica;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albums;

    public AlbumAdapter(List<Album> albums) {
        this.albums = albums != null ? albums : new ArrayList<>();
    }

    public void updateList(List<Album> newAlbums) {
        this.albums = newAlbums != null ? newAlbums : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        if (albums == null || position < 0 || position >= albums.size()) {
            return;
        }

        Album album = albums.get(position);

        holder.tvTitle.setText(album.getTitle());
        holder.tvArtist.setText(album.getArtist());
        holder.tvMeta.setText(String.format(Locale.getDefault(), "%d   %d canciones", album.getYear(), album.getSongCount()));
        holder.tvGenre.setText(album.getGenre());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", album.getRating()));
        holder.tvRatingCount.setText(album.getRatingCount());

        double rating = album.getRating();
        if (rating <= 2.0) {
            holder.tvRating.setTextColor(Color.parseColor("#FF4C4C"));
        } else if (rating <= 3.5) {
            holder.tvRating.setTextColor(Color.parseColor("#FFD966"));
        } else {
            holder.tvRating.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return albums != null ? albums.size() : 0;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist, tvMeta, tvGenre, tvRating, tvRatingCount;

        public AlbumViewHolder(@NonNull View itemView) {
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