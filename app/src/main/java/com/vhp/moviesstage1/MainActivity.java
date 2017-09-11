package com.vhp.moviesstage1;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vhp.moviesstage1.adapter.MoviesAdapter;
import com.vhp.moviesstage1.data.MoviesContract;
import com.vhp.moviesstage1.model.Movies;
import com.vhp.moviesstage1.model.MoviesInfo;
import com.vhp.moviesstage1.model.Result;
import com.vhp.moviesstage1.retrofit.RetrofitAPICaller;
import com.vhp.moviesstage1.utils.Constants;
import com.vhp.moviesstage1.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler ,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "MainActivity";
    private static final int TASK_LOADER_ID = 101;

    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private List<MoviesInfo> moviesInfoList;
    private MoviesAdapter moviesAdapter;
    private boolean isConnected;
    private Button mMostPopularButton;
    private Button mTopRatedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Typecasting
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        // api request to fetch the movies popular API as default
        // makeApiRequest(Constants.MOVIES_POPULAR);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this , 2);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        // Initialize the adapter and attach it to the RecyclerView
        moviesAdapter = new MoviesAdapter(this);
        mMoviesRecyclerView.setAdapter(moviesAdapter);

        // loads the data into UI
        loadMoviesCategory(Constants.MOVIES_POPULAR);

        mMostPopularButton = findViewById(R.id.button_most_popular);
        mMostPopularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoviesCategory(Constants.MOVIES_POPULAR);
                //changing the most popular button to selected mode
                mMostPopularButton.setBackgroundResource(R.drawable.drawable_movies_select);
                mMostPopularButton.setTextColor(getResources().getColor(R.color.colorTextWhite));
                // changing the top rated button to normal mode
                mTopRatedButton.setBackgroundResource(R.drawable.drawable_movies_normal);
                mTopRatedButton.setTextColor(getResources().getColor(R.color.colorTextReviews));
            }
        });

        mTopRatedButton = findViewById(R.id.button_top_rated);
        mTopRatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoviesCategory(Constants.MOVIES_TOP_RATED);
                //changing the top rated button to selected mode
                mTopRatedButton.setBackgroundResource(R.drawable.drawable_movies_select);
                mTopRatedButton.setTextColor(getResources().getColor(R.color.colorTextWhite));
                //changing the top rated button to selected mode
                mMostPopularButton.setBackgroundResource(R.drawable.drawable_movies_normal);
                mMostPopularButton.setTextColor(getResources().getColor(R.color.colorTextReviews));
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main , menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_movies_sort){
            // creating an alertBuilder to show the dialog and process the user selected item
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.title_sort_by));
            builder.setNegativeButton("Cancel", null);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
            arrayAdapter.add(getResources().getString(R.string.title_most_popular));
            arrayAdapter.add(getResources().getString(R.string.title_top_rated));

            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        loadMoviesCategory(Constants.MOVIES_POPULAR);
                    }else if(i==1){
                        loadMoviesCategory(Constants.MOVIES_TOP_RATED);
                    }
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MoviesInfo moviesInfoData) {
        // pass the selected movie data in bundle and start the next activity
        Intent detailsActivityIntent = new Intent(MainActivity.this , DetailsActivity.class);
        detailsActivityIntent.putExtra("MoviesInfo" , moviesInfoData);
        startActivity(detailsActivityIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String movieType = args.getString("MovieCategory");
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mMoviesCursor = null;

            @Override
            protected void onStartLoading() {
                if (mMoviesCursor != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMoviesCursor);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(
                            MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            MoviesContract.MoviesEntry.COLUMN_MOVIE_CATEGORY,
                            new String[]{movieType},
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }


            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                super.deliverResult(data);
                if(data.getCount()==0){
                    Toast.makeText(
                            MainActivity.this ,
                            getResources().getString(R.string.info_no_offline_data) ,
                            Toast.LENGTH_LONG).show();
                }else {
                    mMoviesCursor = data;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
//        if(data.getCount()!=0){
        moviesAdapter.swapCursor(data);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }


    /**
     * Fetches the movie list according to movieCategory
     * @param movieCategory Either Popular Movies or Top rated Movies
     */
    private void fetchMoviesList(final String movieCategory){
        Call<Movies> moviesCall = null;
        if(movieCategory.equalsIgnoreCase(Constants.MOVIES_POPULAR)){
            moviesCall = RetrofitAPICaller.getInstance(MainActivity.this)
                    .getMoviesAPIs().getPopularMoviesListAPI(Constants.API_KEY);

        }else if(movieCategory.equalsIgnoreCase(Constants.MOVIES_TOP_RATED)){
            moviesCall = RetrofitAPICaller.getInstance(MainActivity.this)
                    .getMoviesAPIs().getTopRatedMoviesListAPI(Constants.API_KEY);
        }
        moviesCall.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.d(TAG, "onResponse: " + response);
                insertMoviesResponseIntoDB(response , movieCategory);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t);

            }
        });
    }

    /**
     * This method does following
     * 1.Delete the current DB Data based on MovieType(Popular/Toprated) to stay updated
     * 2.Iterate the response , gets the needed fields to make POJO class
     * 3.Insert the Data into DB via {@link android.content.ContentProvider}
     * 4.Restarts the {@link Loader} to display the current fetched items according to MovieType(Popular/Toprated)
     * @param response API response from the movieDB
     * @param movieCategory Popular or TopRated
     */
    private void insertMoviesResponseIntoDB(Response<Movies> response , String movieCategory) {
        int responseSize = response.body().getResults().size();
        List<Result> mMoviesResultList = response.body().getResults();
        // delete the current DB data according to movietype(Popular or TopRated) to stay updated
        getContentResolver().delete(
                MoviesContract.MoviesEntry.CONTENT_URI ,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_CATEGORY,
                new String[]{movieCategory});
        for (int i = 0; i < responseSize; i++) {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIES_ID ,
                    mMoviesResultList.get(i).getId().toString());
            mContentValues.put(
                    MoviesContract.MoviesEntry.COLUMN_TITLE ,
                    mMoviesResultList.get(i).getOriginalTitle());
            mContentValues.put(
                    MoviesContract.MoviesEntry.COLUMN_PLOT ,
                    mMoviesResultList.get(i).getOverview());
            mContentValues.put(
                    MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE ,
                    mMoviesResultList.get(i).getReleaseDate());
            mContentValues.put(
                    MoviesContract.MoviesEntry.COLUMN_POSTER ,
                    "https://image.tmdb.org/t/p/w500"+mMoviesResultList.get(i).getPosterPath());
            mContentValues.put(
                    MoviesContract.MoviesEntry.COLUMN_USER_RATING ,
                    mMoviesResultList.get(i).getVoteAverage());
            mContentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_CATEGORY ,
                    movieCategory);
            // insert movies data into DB via Content provider
            getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI , mContentValues);
            Log.d(TAG, "insertMoviesResponseIntoDB: " + mMoviesResultList.get(i).getOriginalTitle());
        }

        // bundle the movietype to fetch the movies based on Popular or Top rated
        triggerLoader(movieCategory);
    }

    /**
     * This method loads the data from DB via Content Provider according to movieType
     * @param movieType either Popular/TopRated
     */

    private void triggerLoader(String movieType){
        Bundle mBundle = new Bundle();
        mBundle.putString("MovieCategory" , movieType);
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, mBundle, MainActivity.this);
    }

    /**
     * checks the internet connection and acts as follows
     * 1.internet connected - True - connects to API and fetches response
     * @param movieCategory either Popular/TopRated Movies
     */
    private void loadMoviesCategory(String movieCategory){
        isConnected = NetworkUtils.isNetworkConnected(MainActivity.this);
        if(isConnected){
            fetchMoviesList(movieCategory);
        }else {
            triggerLoader(movieCategory);
        }
    }
}
