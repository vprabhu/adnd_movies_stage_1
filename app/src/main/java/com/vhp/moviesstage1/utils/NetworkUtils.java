package com.vhp.moviesstage1.utils;

import android.net.Uri;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

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
     * @return URL to fetch MOvies list from moviesDB.org
     */
    public static URL buildUrl(String query){
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

        Log.d(TAG, "buildUrl: " + moviesUrl);
        return moviesUrl;
    }



}
