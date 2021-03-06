package com.vhp.moviesstage1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private final int DB_ACCESS = 1;
    private final int API_ACCESS = 0;

    private MoviesAdapter moviesAdapter;
    private Button mMostPopularButton;
    private Button mTopRatedButton;
    private Button mFavouritesButton;
    private String movieType;
    private RecyclerView mMoviesRecyclerView;
    private Parcelable layoutManagerSavedState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Typecasting
        mMoviesRecyclerView = findViewById(R.id.recyclerView_movies);
        // api request to fetch the movies popular API as default
        // makeApiRequest(Constants.MOVIES_POPULAR);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this , 2);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        // Initialize the adapter and attach it to the RecyclerView
        moviesAdapter = new MoviesAdapter(this);
        mMoviesRecyclerView.setAdapter(moviesAdapter);

        if(savedInstanceState==null || !savedInstanceState.containsKey("restoreMoviesList")){
            // loads the data into UI
            loadMoviesCategory(Constants.MOVIES_POPULAR);
            movieType = Constants.MOVIES_POPULAR;
        }

        mMostPopularButton = findViewById(R.id.button_most_popular);
        mMostPopularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserSelectedMovies(
                        Constants.MOVIES_POPULAR ,
                        mMostPopularButton ,
                        mFavouritesButton ,
                        mTopRatedButton);
            }
        });

        mTopRatedButton = findViewById(R.id.button_top_rated);
        mTopRatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserSelectedMovies(
                        Constants.MOVIES_TOP_RATED ,
                        mTopRatedButton ,
                        mFavouritesButton ,
                        mMostPopularButton);

            }
        });

        mFavouritesButton = findViewById(R.id.button_show_favourites);
        mFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserSelectedMovies(
                        Constants.MOVIES_FAVOURITES ,
                        mFavouritesButton ,
                        mTopRatedButton ,
                        mMostPopularButton);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("restoreMoviesList" , movieType);
        outState.putParcelable("recyclerViewScrollPosition", mMoviesRecyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String persistMovieType = savedInstanceState.getString("restoreMoviesList");
        layoutManagerSavedState = ((Bundle) savedInstanceState).getParcelable("recyclerViewScrollPosition");
        if(persistMovieType.equalsIgnoreCase(Constants.MOVIES_POPULAR)){
            loadUserSelectedMovies(
                    Constants.MOVIES_POPULAR ,
                    mMostPopularButton ,
                    mFavouritesButton ,
                    mTopRatedButton);
        }else if(persistMovieType.equalsIgnoreCase(Constants.MOVIES_TOP_RATED)){
            loadUserSelectedMovies(
                    Constants.MOVIES_TOP_RATED ,
                    mTopRatedButton ,
                    mFavouritesButton ,
                    mMostPopularButton);

        }else if(persistMovieType.equalsIgnoreCase(Constants.MOVIES_FAVOURITES)){
            loadUserSelectedMovies(
                    Constants.MOVIES_FAVOURITES ,
                    mFavouritesButton ,
                    mTopRatedButton ,
                    mMostPopularButton);
        }
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
        final int movieSource = args.getInt("MovieSource");

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
                Cursor mCursor = null;
                try {
                    switch (movieSource){
                        case DB_ACCESS:
                            mCursor =  getContentResolver().query(
                                    MoviesContract.MoviesEntry.CONTENT_URI,
                                    null,
                                    MoviesContract.MoviesEntry.COLUMN_FAVOURITES,
                                    new String[]{String.valueOf(1)},
                                    null);
                            break;

                        case API_ACCESS:
                            mCursor = getContentResolver().query(
                                    MoviesContract.MoviesEntry.CONTENT_URI,
                                    null,
                                    MoviesContract.MoviesEntry.COLUMN_MOVIE_CATEGORY,
                                    new String[]{movieType},
                                    null);
                            break;
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
                return mCursor;
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
                    restoreLayoutManagerPosition();
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

        // iterating response list to insert to DB via ContentProvider
        for (int i = 0; i < responseSize; i++) {
            // get the MovieID
            String movieId = mMoviesResultList.get(i).getId().toString();
            //check for favourites
            int favouriteStatus = getMovieFavouriteStatus(movieId);
            // insert the new Movie data which user has not favourite it yet
            if(favouriteStatus==0){
                // deleting the old data to stay updated
                getContentResolver().delete(
                        MoviesContract.MoviesEntry.CONTENT_URI ,
                        MoviesContract.MoviesEntry.COLUMN_MOVIES_ID,
                        new String[]{movieId});
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
//                Log.d(TAG, "insertMoviesResponseIntoDB: " + mMoviesResultList.get(i).getOriginalTitle());
            }
        }
        // bundle the movietype to fetch the movies based on Popular or Top rated
        triggerLoader(API_ACCESS , movieCategory);
    }

    /**
     * This method loads the data from DB via Content Provider according to movieType
     * @param movieType either Popular/TopRated
     */

    private void triggerLoader(int source , String movieType){
        Bundle mBundle = new Bundle();
        mBundle.putString("MovieCategory" , movieType);
        mBundle.putInt("MovieSource" , source);
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, mBundle, MainActivity.this);
    }

    /**
     * checks the internet connection and acts as follows
     * 1.internet connected - True - connects to API and fetches response
     * @param movieCategory either Popular/TopRated Movies
     */
    private void loadMoviesCategory(String movieCategory){
        boolean isConnected = NetworkUtils.isNetworkConnected(MainActivity.this);
        if(isConnected){
            fetchMoviesList(movieCategory);
        }else {
            triggerLoader(API_ACCESS , movieCategory);
        }
    }

    /**
     * check the favourite status on movie
     * @param movieId movieId
     * @return status if status = 1 User Favourite status =0 not Favourite yet
     */
    private int getMovieFavouriteStatus(String movieId){
        int status = 0;
        String[] projection = {MoviesContract.MoviesEntry.COLUMN_FAVOURITES};
        String[] movieIds = {movieId};
        Cursor mCursor = getContentResolver().query( MoviesContract.MoviesEntry.CONTENT_URI ,
                projection ,
                MoviesContract.MoviesEntry.COLUMN_MOVIES_ID ,
                movieIds ,
                null);
        if(mCursor.moveToFirst()){
            status = mCursor.getInt(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_FAVOURITES));
        }
        mCursor.close();
        return status;
    }

    /**
     * loads the user seleccted movie types to RecyclerView
     * @param movieTypeParams movie Type
     *                        either
     *                        1.{@link Constants}.MOVIES_POPULAR
     *                        2.{@link Constants}.MOVIES_TOP_RATED
     *                        2.{@link Constants}.MOVIES_FAVOURITES
     * @param toBeSelected Button's UI to be changed to selected mode
     * @param deselectButton1 Button's UI to be changed to normal mode
     * @param deselectButton2 Button's UI to be changed to normal mode
     */
    private void loadUserSelectedMovies(String movieTypeParams, Button toBeSelected ,
                                        Button deselectButton1 , Button deselectButton2){
        movieType = movieTypeParams;
        if(movieType.equalsIgnoreCase(Constants.MOVIES_FAVOURITES)){
            triggerLoader(DB_ACCESS , "");
        }else {
            loadMoviesCategory(movieTypeParams);
        }
        //change to selected mode
        toBeSelected.setBackgroundResource(R.drawable.drawable_movies_select);
        toBeSelected.setTextColor(getResources().getColor(R.color.colorTextWhite));
        //change to normal mode
        deselectButton1.setBackgroundResource(R.drawable.drawable_movies_normal);
        deselectButton1.setTextColor(getResources().getColor(R.color.colorTextReviews));
        //change to normal mode
        deselectButton2.setBackgroundResource(R.drawable.drawable_movies_normal);
        deselectButton2.setTextColor(getResources().getColor(R.color.colorTextReviews));
    }

    /**
     * restore the recyclerView scroll position
     */
    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            mMoviesRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }
}
