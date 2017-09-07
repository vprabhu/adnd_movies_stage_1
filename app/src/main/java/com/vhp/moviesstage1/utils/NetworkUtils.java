package com.vhp.moviesstage1.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Scanner;

/**
 * Created by root on 14/7/17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Base url for whole app to fetch info about movies based on user selection
     */
    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    /** query parameter used to build the URL */
    private final static String API_KEY = "api_key";


    /**
     * This method collects the query from the user selection(either /movie/popular or
     * movie/top_rated) and completes the URL formation which we require to get the movies list
     * @param query the movie selection either popular or toprated
     * @return URL to fetch Movies list from moviesDB.org
     */
    public static URL buildmovieslisturl(String query){
        Uri UriBuilder = Uri.parse(BASE_URL).buildUpon()
                .appendPath(query)
                .appendQueryParameter(API_KEY , Constants.API_KEY).build();

        URL moviesUrl = null;
        try {
            // URLDecoder is used to avoid the special characters in url
            moviesUrl = new URL(URLDecoder.decode(UriBuilder.toString(), "UTF-8"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "buildmovieslisturl: " + moviesUrl);
        return moviesUrl;
    }

    /**
     * This method helps to get the movies review based on the movie ID
     * @param movieId the movie Id tom fetch the reviews for the respective movies
     * @return URL to fetch Movie reviews from moviesDB.org
     */
    public static URL buildMovieReviewsUrl(String movieId){
        Uri UriBuilder = Uri.parse(BASE_URL).buildUpon()
                .appendPath("movie").appendPath(movieId).appendPath("reviews")
                .appendQueryParameter(API_KEY , Constants.API_KEY).build();

        URL moviesUrl = null;
        try {
            // URLDecoder is used to avoid the special characters in url
            moviesUrl = new URL(URLDecoder.decode(UriBuilder.toString(), "UTF-8"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "buildmovieslisturl: " + moviesUrl);
        return moviesUrl;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
