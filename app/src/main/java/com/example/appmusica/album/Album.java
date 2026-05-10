package com.example.appmusica.album;

public class Album {
    private final String title;
    private final String artist;
    private final int year;
    private final int songCount;
    private final String genre;
    private final double rating;
    private final String ratingCount;

    public Album(String title, String artist, int year, int songCount, String genre, double rating, String ratingCount) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.songCount = songCount;
        this.genre = genre;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getYear() {
        return year;
    }

    public int getSongCount() {
        return songCount;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }
}