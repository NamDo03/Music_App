package com.example.music_app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.databinding.ActivityPlayerBinding;
import com.example.music_app.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable runnable;
    TextView songName, artistName, currentTime, totalTime;
    ImageView coverImage;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, prevBtn;
    boolean isFavorite;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    pausePlayback();
                } else {
                    resumePlayback();
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExoPlayer.skipToNextSong(PlayerActivity.this);
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExoPlayer.skipToPrevSong(PlayerActivity.this);
            }
        });
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar(); // Cập nhật SeekBar
                handler.postDelayed(this, 1000); // Lặp lại sau mỗi giây
            }
        };

        // Xử lý sự kiện cho SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Xử lý sự kiện tua nhạc
                if (fromUser) {
                    seekTo(progress);
                }
            }

            // Xử lý các sự kiện khác của SeekBar
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        updateSongInfo();
    }

    private void initViews() {
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.artist_name);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        coverImage = findViewById(R.id.cover_image);
        nextBtn = findViewById(R.id.btn_next);
        prevBtn = findViewById(R.id.btn_previous);
        pausePlay = findViewById(R.id.btn_play_pause);
        seekBar = findViewById(R.id.seekBar);
    }

    public void updateSongInfo() {
        SongModel currentSong = MyExoPlayer.getCurrentSong();
        if (currentSong != null) {
            pausePlay.setImageResource(R.drawable.ic_pause);
            songName.setText(currentSong.getTitle());
            artistName.setText(currentSong.getSubtitle());
            Glide.with(coverImage).load(currentSong.getCoverUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                    .into(coverImage);
            exoPlayer = MyExoPlayer.getInstance();
        }

        Drawable defaultColor = binding.btnFavorite.getBackground();
        checkInFavorite(currentSong.getId());
        binding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeFromFavorite(currentSong.getId());
                    checkInFavorite(currentSong.getId());
                } else {
                    addToFavorite(currentSong.getId());
                    checkInFavorite(currentSong.getId());
                }
            }
        });
    }

    public void checkInFavorite(String songId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userName = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("favorite").document(userName);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> songs = (List<String>) documentSnapshot.get("songs");
                if (songs != null && songs.contains(songId)) {
                    binding.btnFavorite.setImageResource(R.drawable.ic_heart_solid);
                    isFavorite = true;
                } else {
                    binding.btnFavorite.setImageResource(R.drawable.ic_heart_regular);
                    isFavorite = false;
                }
            }
        });
    }

    public void addToFavorite(String songId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userName = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("favorite").document(userName);

        // Check if the document exists
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, update the "songs" array
                    userDocRef.update("songs", FieldValue.arrayUnion(songId))
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(), "New song added successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Failed to add new song", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Document doesn't exist, create a new document with the song ID
                    Map<String, Object> songData = new HashMap<>();
                    songData.put("songs", Arrays.asList(songId));

                    userDocRef.set(songData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(), "New song added successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Failed to add new song", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error checking document existence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeFromFavorite(String songId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userName = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("favorite").document(userName);

        userDocRef.update("songs", FieldValue.arrayRemove(songId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Song removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to remove song from favorites", Toast.LENGTH_SHORT).show();
                });
    }

    private void pausePlayback() {
        if (exoPlayer != null) {
            exoPlayer.pause();
            isPlaying = false;
            pausePlay.setImageResource(R.drawable.ic_play);
        }
    }

    // Phương thức để tiếp tục phát nhạc
    private void resumePlayback() {
        if (exoPlayer != null) {
            exoPlayer.play();
            isPlaying = true;
            pausePlay.setImageResource(R.drawable.ic_pause);
        }
    }

    // Phương thức để tua nhạc
    private void seekTo(int position) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(position);
            // Cập nhật giao diện người dùng và trạng thái của ExoPlayer
        }
    }

    private void updateSeekBar() {
        if (exoPlayer != null && exoPlayer.getDuration() > 0) {
            int currentPosition = (int) exoPlayer.getCurrentPosition();
            int totalDuration = (int) exoPlayer.getDuration();

            // Cập nhật thời gian đã chạy (currentTime) và tổng thời gian (totalTime)
            currentTime.setText(formatTime(currentPosition));
            totalTime.setText(formatTime(totalDuration));
            if (currentPosition >= totalDuration) {
                MyExoPlayer.skipToNextSong(PlayerActivity.this);
            }
            // Cập nhật vị trí của SeekBar
            seekBar.setProgress(currentPosition);
            seekBar.setMax(totalDuration);
        }
    }

    // Phương thức để chuyển đổi thời gian thành dạng chuỗi (mm:ss)
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000); // Bắt đầu cập nhật SeekBar sau khi resume
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Dừng cập nhật SeekBar khi pause
    }

}
