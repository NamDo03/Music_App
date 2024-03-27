package com.example.music_app;

import android.content.Context;
import android.util.Log;

import com.example.music_app.models.SongModel;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class MyExoPlayer {
    private static ExoPlayer exoPlayer = null;
    private static SongModel currentSong = null;
    public static SongModel getCurrentSong() {
        return currentSong;
    }

    public static ExoPlayer getInstance() {
        return exoPlayer;
    }

    public static void startPlaying(Context context, SongModel song) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }

        if (currentSong != song) {
            currentSong = song;

            if (currentSong != null && currentSong.getUrl() != null) {
                String url = currentSong.getUrl();
                Log.d("MyExoPlayer", " URL: " + url);
                MediaItem mediaItem = MediaItem.fromUri(url);
                if (exoPlayer != null) {
                    Log.d("Player", " Active: ");
                    exoPlayer.setMediaItem(mediaItem);
                    exoPlayer.prepare();
                    exoPlayer.play();
                }
            }
        }
    }
}
