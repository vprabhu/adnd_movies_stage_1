package com.vhp.moviesstage1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 7/23/17.
 */

public class MoviesInfo implements Parcelable{


    public String getMovieId() {
        return movieId;
    }

    private String movieId;
    private String movieTitle;
    private String moviePoster;
    private String moviePlot;
    private String movieUserRating;
    private String movieReleaseDate;


    public MoviesInfo(String movieId, String movieTitle, String moviePoster, String moviePlot,
                      String movieUserRating, String movieReleaseDate) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.moviePlot = moviePlot;
        this.movieUserRating = movieUserRating;
        this.movieReleaseDate = movieReleaseDate;
    }

    protected MoviesInfo(Parcel in) {
        movieId = in.readString();
        movieTitle = in.readString();
        moviePoster = in.readString();
        moviePlot = in.readString();
        movieUserRating = in.readString();
        movieReleaseDate = in.readString();
    }

    public static final Creator<MoviesInfo> CREATOR = new Creator<MoviesInfo>() {
        @Override
        public MoviesInfo createFromParcel(Parcel in) {
            return new MoviesInfo(in);
        }

        @Override
        public MoviesInfo[] newArray(int size) {
            return new MoviesInfo[size];
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
        parcel.writeString(movieId);
        parcel.writeString(movieTitle);
        parcel.writeString(moviePoster);
        parcel.writeString(moviePlot);
        parcel.writeString(movieUserRating);
        parcel.writeString(movieReleaseDate);
    }
}
