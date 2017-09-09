package com.vhp.moviesstage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vicky on 8/9/17.
 */

public class MoviesContract {

    // The authority, which is how code knows which Content Provider to access
    public static final String AUTHORITY = "com.vhp.moviesstage1";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "movies" directory
    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns {
        //MoviesEntry is an inner class that defines the contents of the movies table */
        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // Task table and column names
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIES_ID = "movies_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_MOVIE_CATEGORY = "category";
        public static final String COLUMN_FAVOURITES = "favourites";

    }
}
