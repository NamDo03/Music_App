package com.example.music_app;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;
    boolean isFavorite;

    @OptIn(markerClass = UnstableApi.class)
    @Override
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

        Drawable defaultColor = binding.btnFavorite.getBackground();
        checkInFavorite(currentSong.getId(), defaultColor);
        binding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeFromFavorite(currentSong.getId());
                    checkInFavorite(currentSong.getId(), defaultColor);
                } else {
                    addToFavorite(currentSong.getId());
                    checkInFavorite(currentSong.getId(), defaultColor);
                }
            }
        });
    }

    public void checkInFavorite(String songId, Drawable defaultColor) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userName = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("favorite").document(userName);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> songs = (List<String>) documentSnapshot.get("songs");
                if (songs != null && songs.contains(songId)) {
                    binding.btnFavorite.setBackgroundColor(Color.GREEN);
                    isFavorite = true;
                }
                else {
                    binding.btnFavorite.setBackground(defaultColor);
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


}