package com.vhp.moviesstage1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 7/23/17.
 */

public class Movies implements Parcelable{

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

    protected Movies(Parcel in) {
        movieTitle = in.readString();
        moviePoster = in.readString();
        moviePlot = in.readString();
        movieUserRating = in.readString();
        movieReleaseDate = in.readString();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieTitle);
        parcel.writeString(moviePoster);
        parcel.writeString(moviePlot);
        parcel.writeString(movieUserRating);
        parcel.writeString(movieReleaseDate);
    }
}
