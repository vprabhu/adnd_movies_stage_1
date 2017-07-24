package com.vhp.moviesstage1.model;

/**
 * Created by root on 7/23/17.
 */

public class Movies {

    private String movieTitle;
    private String moviePoster;
    private String moviePlot;
    private String movieUserRating;
    private String movieReleaseDate;


    public Movies(String movieTitle, String moviePoster, String moviePlot, String movieUserRating, String movieReleaseDate) {
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.moviePlot = moviePlot;
        this.movieUserRating = movieUserRating;
        this.movieReleaseDate = movieReleaseDate;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public String getMovieUserRating() {
        return movieUserRating;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

}
