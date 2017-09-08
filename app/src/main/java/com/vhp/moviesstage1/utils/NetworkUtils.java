package com.vhp.moviesstage1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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



    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";




    /**
     * This method collects the query from the user selection(either /movie/popular or
     * movie/top_rated) and completes the URL formation which we require to get the movies list
     * @param query the movie selection either popular or toprated
     * @return URL to fetch MoviesInfo list from moviesDB.org
     */
    public static URL buildmovieslisturl(String query){
        Uri UriBuilder = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendPath(query)
                .appendQueryParameter(Constants.API_KEY_LABEL , Constants.API_KEY).build();

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
    public static URL buildMovieRelatedInfoUrl(String movieId , String info){
        Uri UriBuilder = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendPath("movie").appendPath(movieId).appendPath(info)
                .appendQueryParameter(Constants.API_KEY_LABEL , Constants.API_KEY).build();

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
     * This method helps to get the movie's trailer youtube url which helps to play trailers
     * @param youtubeId the youtube video ID
     * @return URL to fetch Movie Trailers to play in Youtube
     */
    public static URL buildMovieTrailerUrl(String youtubeId){
        Uri UriBuilder = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter("v" , youtubeId).build();

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

    /**
     * Checks weather the internet connection is available
     * */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
