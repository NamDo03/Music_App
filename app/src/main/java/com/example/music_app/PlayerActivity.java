package com.example.music_app;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.databinding.ActivityPlayerBinding;
import com.example.music_app.models.SongModel;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;

    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SongModel currentSong = MyExoPlayer.getCurrentSong();
        if (currentSong != null) {
            binding.songName.setText(currentSong.getTitle());
            binding.artistName.setText(currentSong.getSubtitle());
            Glide.with(binding.coverImage).load(currentSong.getCoverUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(binding.coverImage);
            exoPlayer = MyExoPlayer.getInstance();
            binding.playerControl.showController();
            binding.playerControl.setPlayer(exoPlayer);
        }
    }

}
