package com.vhp.moviesstage1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vhp.moviesstage1.adapter.MoviesAdapter;
import com.vhp.moviesstage1.model.Movies;
import com.vhp.moviesstage1.utils.Constants;
import com.vhp.moviesstage1.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mMoviesRecyclerView;
    private List<Movies> moviesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movies);

        URL moviesUrl = NetworkUtils.buildUrl(Constants.MOVIES_POPULAR);

        new MoviesListAsyncTask().execute(moviesUrl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_movies_sort){
            Toast.makeText(MainActivity.this , "Sorting" ,Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MoviesListAsyncTask extends AsyncTask<URL , String  , String>{

        @Override
        protected String doInBackground(URL... params) {
            String moviesResult = null;
            try {
                moviesResult = NetworkUtils.getResponseFromHttpUrl(params[0]);
                JSONObject mMoviesJsonArray = new JSONObject(moviesResult);
                JSONArray mResultsJsonArray = mMoviesJsonArray.getJSONArray("results");
                for (int i = 0; i < mResultsJsonArray.length(); i++) {
                    JSONObject movieObject = mResultsJsonArray.getJSONObject(i);
                    String title = movieObject.getString("original_title");
                    String poster =movieObject.getString("poster_path");
                    String plot =movieObject.getString("overview");
                    String rating =movieObject.getString("vote_count");
                    String releaseDate =movieObject.getString("release_date");

                    Movies movies = new Movies(title , poster , plot , rating , releaseDate);
                    moviesList.add(movies);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + moviesList.size());
            MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this , moviesList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this , 2);
            mMoviesRecyclerView.setLayoutManager(gridLayoutManager);
            mMoviesRecyclerView.setAdapter(moviesAdapter);
        }
    }
}
