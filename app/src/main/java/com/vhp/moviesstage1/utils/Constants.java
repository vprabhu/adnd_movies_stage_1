package com.vhp.moviesstage1.utils;

/**
 * Created by root on 14/7/17.
 */

public class Constants {

    /** final Constant String to fetch the top rated movies */
    public static final String MOVIES_TOP_RATED = "movie/top_rated";
    /**  API Key for tmdb.org */
    public static final String API_KEY = "579b35a10b849df06371216c8d614ce1" +
            "";
    /** final Constant String to fetch the popoular movies */
    public static final String MOVIES_POPULAR = "movie/popular";
    /**
     * Base url for whole app to fetch info about movies based on user selection
     */
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    /** query parameter used to build the URL */
    public final static String API_KEY_LABEL = "api_key";

    /** constant to fetch the movie reviews data */
    public final static String MOVIES_REVIEWS = "movie/{moviesId}/reviews";

    /** constant to fetch the movie reviews data */
    public final static String MOVIES_TRAILERS = "movie/{moviesId}/videos";

    /** final Constant String to fetch the user's favourite movies*/
    public static final String MOVIES_FAVOURITES = "movie/FAVOURITE";
}
