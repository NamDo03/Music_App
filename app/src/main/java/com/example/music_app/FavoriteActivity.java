package com.example.music_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music_app.adapter.FavoriteApdapter;
import com.example.music_app.databinding.ActivityFavoriteBinding;
import com.example.music_app.models.FavoriteModel;


public class FavoriteActivity extends AppCompatActivity {
    private ActivityFavoriteBinding binding;
    private static FavoriteApdapter favoriteApdapter;
    private static FavoriteModel favorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSongsListRecyclerView();
    }
    void setupSongsListRecyclerView() {
        favoriteApdapter = new FavoriteApdapter(favorite.getSongs());
        binding.songsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.songsListRecyclerView.setAdapter(favoriteApdapter);
    }

    public static void setFavorite(FavoriteModel value) {
        favorite = value;
    }
}
