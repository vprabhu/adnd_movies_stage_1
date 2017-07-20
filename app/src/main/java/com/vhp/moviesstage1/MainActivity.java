package com.vhp.moviesstage1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.vhp.moviesstage1.utils.Constants;
import com.vhp.moviesstage1.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mMoviesRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movies);

        new MoviesListAsyncTask().execute();

    }

    private class MoviesListAsyncTask extends AsyncTask<URL , String  , String>{

        @Override
        protected String doInBackground(URL... params) {
            URL moviesUrl = NetworkUtils.buildUrl(Constants.MOVIES_POPULAR);

            String moviesResult = null;
            try {
                moviesResult = NetworkUtils.getResponseFromHttpUrl(moviesUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);

        }
    }
}
