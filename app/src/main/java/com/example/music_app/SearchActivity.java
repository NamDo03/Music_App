package com.example.music_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.example.music_app.models.SongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.music_app.adapter.FilteredSongListAdapter;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private List<SongModel> allSongsList;
    public List<SongModel> filteredList;
    private FilteredSongListAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        searchEditText = findViewById(R.id.search_edit_text);
        searchRecyclerView = findViewById(R.id.search_recycler_view);

        // Initialize RecyclerView
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new FilteredSongListAdapter(new ArrayList<>()); // Thay vì SectionSongListAdapter
        searchRecyclerView.setAdapter(searchAdapter);

        // Load all songs initially
        loadAllSongs();

        // Set up search functionality
        setupSearch();
    }

    private void loadAllSongs() {
        FirebaseFirestore.getInstance().collection("songs")
                .orderBy("title")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Convert queryDocumentSnapshots to a list of SongModel objects
                        allSongsList = queryDocumentSnapshots.toObjects(SongModel.class);

                        // Log the number of songs retrieved
                        Log.d("Firestore", "Number of songs retrieved: " + allSongsList.size());

                        // Log each song title
                        for (SongModel song : allSongsList) {
                            Log.d("Firestore", "Song title: " + song.getTitle());
                        }

                        searchAdapter.setFilteredSongList(allSongsList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log if there's an error retrieving songs from Firestore
                        Log.e("Firestore", "Error retrieving songs: " + e.getMessage());
                    }
                });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterSongs(String searchText) {
        filteredList = new ArrayList<>();
        if (searchText.isEmpty()) {
            // Nếu searchText rỗng, hiển thị tất cả bài hát
            filteredList.addAll(allSongsList);
        } else {
            // Áp dụng bộ lọc
            for (SongModel song : allSongsList) {
                if (song.getTitle().toLowerCase().contains(searchText.toLowerCase()) || song.getSubtitle().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(song);
                    Log.d("SearchActivity", "Song title: " + song.getTitle());
                }
            }
        }
        for (SongModel song : filteredList) {
            Log.d("SearchActivity", "LIST: " + song.getTitle());
        }
        searchAdapter.setFilteredSongList(filteredList);
    }
}

