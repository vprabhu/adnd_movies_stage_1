package com.vhp.moviesstage1;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

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

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler{

    private static final String TAG = "MainActivity";

    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private List<Movies> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Typecasting
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        // api request to fetch the movies popular API as default
        makeApiRequest(Constants.MOVIES_POPULAR);
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
            // create=ing an alertBuilder to show the dialog and process the user selected item
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.title_sort_by));
            builder.setNegativeButton("Cancel", null);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
            arrayAdapter.add(getResources().getString(R.string.title_most_popular));
            arrayAdapter.add(getResources().getString(R.string.info_top_rated));

            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        makeApiRequest(Constants.MOVIES_POPULAR);
                    }else if(i==1){
                        makeApiRequest(Constants.MOVIES_TOP_RATED);
                    }
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movies moviesData) {
        Log.d(TAG, "onClick: " + moviesData);
    }

    /**
     * AsyncTask to load the RecyclerView with list of parsed movies Json data from the API
     */
    private class MoviesListAsyncTask extends AsyncTask<URL , String  , String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // the visibility is triggered between the progressbar and recyclerview to show
            // progressbar
            mProgressBar.setVisibility(View.VISIBLE);
            mMoviesRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(URL... params) {
            String moviesResult = null;
            moviesList = new ArrayList<>();
            // the api response is json parsed and added the all the movies in the list
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
            // the visibility is triggered between the progressbar and recyclerview to show the
            // updated recyclerview
            mProgressBar.setVisibility(View.GONE);
            mMoviesRecyclerView.setVisibility(View.VISIBLE);
            // create an adapter which takes the moviesList and inflates the view with data
            MoviesAdapter moviesAdapter = new MoviesAdapter(moviesList , MainActivity.this);
            // create the grid layout with the columns of 2 to display GridView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this , 2);
            // assign the gridLayoutManager to recyclerview
            mMoviesRecyclerView.setLayoutManager(gridLayoutManager);
            // set the adapter to recyclerView
            mMoviesRecyclerView.setAdapter(moviesAdapter);
        }
    }

    /**
     * fetches the movies list according to the user's selection and displays the respective
     * list of movies
     * @param requestType type of movies the user need is known by this parameter
     */
    private void makeApiRequest(String requestType){
        URL moviesUrl = NetworkUtils.buildUrl(requestType);
        new MoviesListAsyncTask().execute(moviesUrl);
    }
}
