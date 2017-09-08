package com.vhp.moviesstage1;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

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

        boolean isConnected =  NetworkUtils.isNetworkConnected(MainActivity.this);
        if(isConnected){
            fetchMoviesList("");
        }else {
         /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
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
            // creating an alertBuilder to show the dialog and process the user selected item
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
//                        makeApiRequest(Constants.MOVIES_POPULAR);
                    }else if(i==1){
//                        makeApiRequest(Constants.MOVIES_TOP_RATED);
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
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }


            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMoviesCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        moviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }


    private void fetchMoviesList(String movieType){
        Call<Movies> moviesCall = RetrofitAPICaller.getInstance(MainActivity.this)
                .getMoviesAPIs().getMoviesListAPI(Constants.API_KEY);
        moviesCall.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.d(TAG, "onResponse: " + response);
                insertMoviesResponseIntoDB(response);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t);

            }
        });
    }

    private void insertMoviesResponseIntoDB(Response<Movies> response) {
        int responseSize = response.body().getResults().size();
        List<Result> mMoviesResultList = response.body().getResults();
        // check if the response size is greater than zero and delete the current DB data
        if(mMoviesResultList.size()>0){
            getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI , null , null);
        }
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
            // insert movies data into DB via Content provider
            Uri mUri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI , mContentValues);
            Log.d(TAG, "insertMoviesResponseIntoDB: " + mUri);
        }
        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }
}
