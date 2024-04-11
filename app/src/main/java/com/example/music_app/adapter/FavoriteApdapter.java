package com.example.music_app.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.MyExoPlayer;
import com.example.music_app.PlayerActivity;
import com.example.music_app.databinding.FavoriteSongListRecyclerRowBinding;
import com.example.music_app.models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoriteApdapter extends RecyclerView.Adapter<FavoriteApdapter.MyViewHolder>{
    private List<String> songIdList;

    public FavoriteApdapter(List<String> songIdList) {
        this.songIdList = songIdList;
    }

    @Override
    public FavoriteApdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FavoriteSongListRecyclerRowBinding binding = FavoriteSongListRecyclerRowBinding.inflate(inflater, parent, false);
        return new FavoriteApdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FavoriteApdapter.MyViewHolder holder, int position) {
        String songId = songIdList.get(position);
        holder.bindData(songId);
    }

    @Override
    public int getItemCount() {
        return songIdList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private FavoriteSongListRecyclerRowBinding binding;

        public MyViewHolder(FavoriteSongListRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(String songId) {
            FirebaseFirestore.getInstance().collection("songs")
                    .document(songId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        SongModel song = documentSnapshot.toObject(SongModel.class);
                        if (song != null) {
                            binding.songTitleTextView.setText(song.getTitle());
                            binding.songSubtitleTextView.setText(song.getSubtitle());
                            Glide.with(binding.songCoverImageView)
                                    .load(song.getCoverUrl())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                                    .into(binding.songCoverImageView);
                            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MyExoPlayer.startPlaying(view.getContext(), song);
                                    view.getContext().startActivity(new Intent(view.getContext(), PlayerActivity.class));
                                }
                            });
                        }
                    });
        }
    }
}
