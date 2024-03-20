package com.example.music_app.models;

import java.util.Collections;
import java.util.List;

public class CategoryModel {
    private String name;
    private String coverUrl;
    private List<String> songs;


    public CategoryModel() {
        this.name = "";
        this.coverUrl = "";
        this.songs= Collections.singletonList("");
    }

    public CategoryModel(String name, String coverUrl,List<String> songs) {
        this.name = name;
        this.coverUrl = coverUrl;
        this.songs=songs;
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
}