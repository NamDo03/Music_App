package com.example.music_app.models;

import java.util.Collections;
import java.util.List;

public class FavoriteModel {
    private List<String> songs;


    public FavoriteModel() {
        this.songs= Collections.singletonList("");
    }

    public FavoriteModel(List<String> songs) {
        this.songs=songs;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
}
