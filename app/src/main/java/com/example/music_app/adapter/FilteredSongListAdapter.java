package com.example.music_app.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.MyExoPlayer;
import com.example.music_app.PlayerActivity;
import com.example.music_app.R;
import com.example.music_app.models.SongModel;

import java.util.List;

public class FilteredSongListAdapter extends RecyclerView.Adapter<FilteredSongListAdapter.SongViewHolder> {

    private List<SongModel> filteredSongList;

    public FilteredSongListAdapter(List<SongModel> filteredSongList) {
        this.filteredSongList = filteredSongList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongModel song = filteredSongList.get(position);
        holder.bindData(song);
    }

    @Override
    public int getItemCount() {
        return filteredSongList.size();
    }

    public void setFilteredSongList(List<SongModel> filteredSongList) {
        this.filteredSongList = filteredSongList;
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView songCoverImageView;
        private TextView songTitleTextView;
        private TextView songArtistTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songCoverImageView = itemView.findViewById(R.id.song_cover_image);
            songTitleTextView = itemView.findViewById(R.id.song_title);
            songArtistTextView = itemView.findViewById(R.id.song_artist);
        }

        public void bindData(SongModel song) {
            // Set song title and artist in the same line
            String titleArtist = song.getTitle();
            String titleTacGia = song.getSubtitle();
            songTitleTextView.setText(titleArtist);
            songArtistTextView.setText(titleTacGia);
            Glide.with(songCoverImageView)
                    .load(song.getCoverUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(songCoverImageView);
            itemView.setOnClickListener(view -> {
                MyExoPlayer.startPlaying(view.getContext(), song);
                view.getContext().startActivity(new Intent(view.getContext(), PlayerActivity.class));
            });
        }
    }
}
