package com.example.music_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.adapter.SongsListAdapter;
import com.example.music_app.databinding.ActivitySongsListBinding;
import com.example.music_app.models.CategoryModel;

public class SongsListActivity extends AppCompatActivity {

    private ActivitySongsListBinding binding=null;
    private static CategoryModel category;
    private static SongsListAdapter songsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.nameTextView.setText(category.getName());
        Glide.with(binding.coverImageView).load(category.getCoverUrl())
                .apply(
                        new RequestOptions().transform(new RoundedCorners(32))
                )
                .into(binding.coverImageView);

        setupSongsListRecyclerView();
    }

    void setupSongsListRecyclerView(){
        songsListAdapter = new SongsListAdapter(category.getSongs());
        binding.songsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.songsListRecyclerView.setAdapter(songsListAdapter);
    }


    public static CategoryModel getCategory() {
        return category;
    }

    public static void setCategory(CategoryModel value) {
        category = value;
    }
}