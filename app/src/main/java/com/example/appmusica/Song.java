package com.example.appmusica;

public class Song {
    private final String title;
    private final String artist;
    private final String album;
    private final String duration;
    private final int year;
    private final String genre;
    private final double rating;
    private final String ratingCount;

    public Song(String title, String artist, String album, String duration, int year, String genre, double rating, String ratingCount) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
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

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public int getYear() {
        return year;
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