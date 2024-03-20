package com.example.music_app;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art;
    ImageButton nextBtn, prevBtn, backBtn,shuffleBtn, repeatBtn, playBtn;
    SeekBar seekBar;
    int possition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_player_view);
        initViews();
        getIntentMethod();
    }

    private void getIntentMethod() {
        possition = getIntent().getIntExtra("position", -1);
    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.artist_name);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        cover_art = findViewById(R.id.card_image_container);
        nextBtn = findViewById(R.id.btn_skip_next);
        prevBtn = findViewById(R.id.btn_skip_previous);
        backBtn = findViewById(R.id.btn_back);
        shuffleBtn = findViewById(R.id.btn_shuffle);
        repeatBtn = findViewById(R.id.btn_repeat);
        playBtn = findViewById(R.id.btn_play_pause);
        seekBar = findViewById(R.id.seekBar);
    }

}
