package com.example.music_app;

import android.content.Context;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.music_app.models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyExoPlayer {
    private static ExoPlayer exoPlayer = null;
    private static SongModel currentSong = null;
    private static String currentSongId = null;
    private static List<String> songIdList = null;

    public static void setSongIdList(List<String> list) {
         songIdList = list;
    }
    public static void setCurrentSongId(String songId) {
        currentSongId = songId;
    }
    public static SongModel getCurrentSong() {
        Log.d("MyExo","currentSong:" +currentSong);
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
                } else {
                    Log.e("ExoPlayer", "ExoPlayer instance is null!");
                }
            }else  {
                Log.e("ExoPlayer", "Current song or its URL is null!");
            }
        }else  {
            Log.d("ExoPlayer", "Song is already playing: " + currentSong.getTitle());
        }
    }

    public static void skipToNextSong(Context context) {
        if (songIdList != null && !songIdList.isEmpty()) {
            int currentIndex = songIdList.indexOf(currentSongId);
            int nextIndex = (currentIndex + 1) % songIdList.size();
            String nextSongId = songIdList.get(nextIndex);
            Log.d("MyExo","curSong:"+ currentIndex + " nextSong:"+ nextIndex + " size List:"+ songIdList.size());
            Log.d("MyExo","nextSongId:"+ nextSongId);

            FirebaseFirestore.getInstance().collection("songs")
                    .document(nextSongId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        SongModel nextSongWithDetails = documentSnapshot.toObject(SongModel.class);
                        Log.d("MyExo","nextSong:"+ nextSongWithDetails);
                        if (nextSongWithDetails != null) {
                            currentSongId = nextSongId;
                            Log.d("MyExo","nextsong:" + nextSongWithDetails.getTitle());
                            startPlaying(context, nextSongWithDetails);
                        }
                    });
        }
    }
}
