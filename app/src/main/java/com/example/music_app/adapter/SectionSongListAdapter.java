package com.example.music_app.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.MyExoPlayer;
import com.example.music_app.PlayerActivity;
import com.example.music_app.databinding.SectionSongListRecyclerRowBinding;
import com.example.music_app.models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SectionSongListAdapter extends RecyclerView.Adapter<SectionSongListAdapter.MyViewHolder> {

    private List<String> songIdList;
    private List<SongModel> songList;

    public void setSongList(List<SongModel> songList) {
        this.songList = songList;
        notifyDataSetChanged(); // Update RecyclerView
    }

    public SectionSongListAdapter(List<String> songIdList) {
        Log.d("TAG", "Song ID List: " + songIdList.toString());
        this.songIdList = songIdList;
        MyExoPlayer.setSongIdList(songIdList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SectionSongListRecyclerRowBinding binding = SectionSongListRecyclerRowBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String songId = songIdList.get(position);
        holder.bindData(songId);
    }

    @Override
    public int getItemCount() {
        return songIdList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SectionSongListRecyclerRowBinding binding;

        public MyViewHolder(SectionSongListRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bindData(String songId) {
            FirebaseFirestore.getInstance().collection("songs")
                    .document(songId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        SongModel song = documentSnapshot.toObject(SongModel.class);
                        if (song != null) {
                            // Set song title and artist in the same line
                            String titleArtist = song.getTitle() + " - " + song.getSubtitle();
                            binding.songTitleTextView.setText(titleArtist);
                            Glide.with(binding.songCoverImageView)
                                    .load(song.getCoverUrl())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                                    .into(binding.songCoverImageView);
                            binding.getRoot().setOnClickListener(view -> {
                                MyExoPlayer.setCurrentSongId(songId);
                                MyExoPlayer.startPlaying(view.getContext(), song);
                                view.getContext().startActivity(new Intent(view.getContext(), PlayerActivity.class));
                            });
                        }
                    });
        }

    }
}
