package com.example.music_app;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_app.adapter.CategoryAdapter;
import com.example.music_app.adapter.SectionSongListAdapter;
import com.example.music_app.databinding.ActivityMainBinding;
import com.example.music_app.models.CategoryModel;
import com.example.music_app.models.FavoriteModel;
import com.example.music_app.models.SongModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NetworkConnection networkConnection;
    private ActivityMainBinding binding;
    private CategoryAdapter categoryAdapter;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        networkConnection = new NetworkConnection();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        if (networkConnection.isNetworkAvailable(this)) {
            setContentView(binding.getRoot());
        } else {
            setContentView(R.layout.network_error_layout);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home_nav) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.search_nav) {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.favorite_nav) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    String userName = user.getUid();
                    setupFavorite(userName);
                }

                return false;
            }
        });

        getCategories();
        setupSection("section_1", binding.section1MainLayout, binding.section1Title, binding.section1RecyclerView);
        setupSection("section_2", binding.section2MainLayout, binding.section2Title, binding.section2RecyclerView);
        setupSection("section_3", binding.section3MainLayout, binding.section3Title, binding.section3RecyclerView);

        binding.optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        showPlayerView();
    }

    private void showPlayerView() {
        binding.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        SongModel currentSong = MyExoPlayer.getCurrentSong();
        if (currentSong != null) {
            binding.playerView.setVisibility(View.VISIBLE);
            binding.songTitleTextView.setText("Now Playing: " + currentSong.getTitle());
            RequestOptions options = new RequestOptions().transform(new RoundedCorners(32));
            Glide.with(binding.songCoverImageView).load(currentSong.getCoverUrl()).apply(options).into(binding.songCoverImageView);
        } else {
            binding.playerView.setVisibility(View.GONE);
        }
    }

    public void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.optionBtn);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.option_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    logout();
                    return true;
                }
                return false;
            }
        });
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void setupCategoryRecyclerView(List<CategoryModel> categoryList) {
        categoryAdapter = new CategoryAdapter(categoryList);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    public void getCategories() {
        FirebaseFirestore.getInstance().collection("category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<CategoryModel> categoryList = queryDocumentSnapshots.toObjects(CategoryModel.class);
                        setupCategoryRecyclerView(categoryList);
                    }
                });
    }
    public void setupFavorite(String id) {
        FirebaseFirestore.getInstance().collection("favorite")
                .document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    FavoriteModel favorite = documentSnapshot.toObject(FavoriteModel.class);
                    if (favorite != null) {
                        FavoriteActivity.setFavorite(favorite);
                        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                        startActivity(intent);
                    }
                });
    }
    public void setupSection(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                    if (section != null) {
                        mainLayout.setVisibility(View.VISIBLE);
                        titleView.setText(section.getName());
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setAdapter(new SectionSongListAdapter(section.getSongs()));
                        mainLayout.setOnClickListener(view -> {
                            SongsListActivity.setCategory(section);
                            startActivity(new Intent(MainActivity.this, SongsListActivity.class));
                        });
                    }
                });
    }

    public void setupMostlyPlayed(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    FirebaseFirestore.getInstance().collection("songs")
                            .orderBy("count", Query.Direction.DESCENDING)
                            .limit(5)
                            .get().addOnSuccessListener(songListSnapshot -> {
                                List<SongModel> songsModelList = songListSnapshot.toObjects(SongModel.class);
                                List<String> songsIdList = new ArrayList<>();
                                for (SongModel songModel : songsModelList) {
                                    songsIdList.add(songModel.getId());
                                }
                                CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                                if (section != null) {
                                    section.setSongs(songsIdList);
                                    mainLayout.setVisibility(View.VISIBLE);
                                    titleView.setText(section.getName());
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                    recyclerView.setAdapter(new SectionSongListAdapter(section.getSongs()));
                                    mainLayout.setOnClickListener(view -> {
                                        SongsListActivity.setCategory(section);
                                        startActivity(new Intent(MainActivity.this, SongsListActivity.class));
                                    });
                                }
                            });
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnection,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkConnection);
    }
}