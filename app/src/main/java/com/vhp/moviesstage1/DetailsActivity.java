package com.vhp.moviesstage1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vhp.moviesstage1.adapter.MovieReviewsAdapter;
import com.vhp.moviesstage1.adapter.MovieTrailersAdapter;
import com.vhp.moviesstage1.model.MovieReviews;
import com.vhp.moviesstage1.model.MovieTrailers;
import com.vhp.moviesstage1.model.Movies;
import com.vhp.moviesstage1.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements MovieTrailersAdapter.MoviesTrailersAdapterOnClickHandler {

    private String movieId;
    private List<MovieReviews> movieReviewsList;
    private List<MovieTrailers> movieTrailersList;
    private Movies movieBasicDetails;
    private RecyclerView mReviewsRecyclerView , mTrailersRecyclerView;
    private View mRelatedInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // getting the movie data from budle which is passed from MainActivity
        Bundle data = getIntent().getExtras();
        movieBasicDetails = data.getParcelable("Movies");
        movieId = movieBasicDetails.getMovieId();

        // set activity title as selected movie name
        getSupportActionBar().setTitle(movieBasicDetails.getMovieTitle());
        // set back button
        getSupportActionBar().setHomeButtonEnabled(true);

        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movie_reviews);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movie_trailers);
        mRelatedInfoView = (View) findViewById(R.id.view_related_info);

        makeMovieReviewsApiRequest(movieId);
    }

    /**
     * fetches the movies list according to the user's selection and displays the respective
     * list of movies
     * @param movieId movieId on which the reviews must be fetched
     */
    private void makeMovieReviewsApiRequest(String movieId){
        URL movieReviewsUrl = NetworkUtils.buildMovieRelatedInfoUrl(movieId , "reviews");
        URL moviesTrailersUrl = NetworkUtils.buildMovieRelatedInfoUrl(movieId , "videos");
//        Log.d(DetailsActivity.class.getSimpleName(), "makeMovieReviewsApiRequest: " +moviesUrl);
        new MoviesRelatedInfoAsyncTask().execute(movieReviewsUrl , moviesTrailersUrl);
    }

    @Override
    public void onTrailerClick(String youtubeLink) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeLink));
        startActivity(appIntent);
    }

    /**
     * AsyncTask to load the movie reviews Json data from the API
     */
    private class MoviesRelatedInfoAsyncTask extends AsyncTask<URL , String  , String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // the visibility is triggered between the progressbar and recyclerview to show
            // progressbar
//            mProgressBar.setVisibility(View.VISIBLE);
//            mMoviesRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(URL... params) {
            String moviesResult = null;
            movieReviewsList = new ArrayList<>();
            movieTrailersList = new ArrayList<>();
            // the api response is json parsed and added the all the movies in the list
            try {
                moviesResult = NetworkUtils.getResponseFromHttpUrl(params[0]);
                JSONObject mMoviesJsonArray = new JSONObject(moviesResult);
                JSONArray mResultsJsonArray = mMoviesJsonArray.getJSONArray("results");
                for (int i = 0; i < mResultsJsonArray.length(); i++) {
                    JSONObject movieObject = mResultsJsonArray.getJSONObject(i);
                    String author = movieObject.getString("author");
                    String content = movieObject.getString("content");

                    MovieReviews movieReviews = new MovieReviews(author , content);
                    movieReviewsList.add(movieReviews);
                }

                moviesResult = NetworkUtils.getResponseFromHttpUrl(params[1]);
                JSONObject mMovieTrailersJsonArray = new JSONObject(moviesResult);
                JSONArray mMoviesTrailersJsonArray= mMovieTrailersJsonArray.getJSONArray("results");
                for (int i = 0; i < mResultsJsonArray.length(); i++) {
                    JSONObject movieObject = mMoviesTrailersJsonArray.getJSONObject(i);
                    String key = movieObject.getString("key");
                    String name = movieObject.getString("name");

                    MovieTrailers movieTrailers = new MovieTrailers(key , name);
                    movieTrailersList.add(movieTrailers);
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
            Log.d(DetailsActivity.class.getSimpleName(), "onPostExecute: " + movieReviewsList.size());


            // the visibility is triggered between the progressbar and recyclerview to show the
            // updated recyclerview
//            mProgressBar.setVisibility(View.GONE);
//            mMoviesRecyclerView.setVisibility(View.VISIBLE);
            // create an adapter which takes the moviesList and inflates the view with data
            MovieReviewsAdapter mMoviesReviews=
                    new MovieReviewsAdapter(movieReviewsList);
            // create the grid layout with the columns of 2 to display GridView
            LinearLayoutManager gridLayoutManager = new LinearLayoutManager(
                    DetailsActivity.this ,
                    LinearLayoutManager.VERTICAL , false);
            // assign the gridLayoutManager to recyclerview
            mReviewsRecyclerView.setLayoutManager(gridLayoutManager);
            // set the adapter to recyclerView
            mReviewsRecyclerView.setAdapter(mMoviesReviews);

            MovieTrailersAdapter mMovieTrailersAdapter=
                    new MovieTrailersAdapter(movieTrailersList, DetailsActivity.this);
            // create the grid layout with the columns of 2 to display GridView
            LinearLayoutManager moviewTrailerLayoutManager = new LinearLayoutManager(
                    DetailsActivity.this ,
                    LinearLayoutManager.VERTICAL , false);
            // assign the gridLayoutManager to recyclerview
            mTrailersRecyclerView.setLayoutManager(moviewTrailerLayoutManager);
            // set the adapter to recyclerView
            mTrailersRecyclerView.setAdapter(mMovieTrailersAdapter);

            if(movieReviewsList.size()==0 && movieTrailersList.size()==0){
                mRelatedInfoView.setVisibility(View.GONE);
            }

            // sets the movie basic details
            bindDataToUI();
        }
    }



    private void  bindDataToUI(){
        // UI Typecasting
        ImageView mMoviePosterImageView = (ImageView) findViewById(R.id.imageView_detail);
        TextView mTitleTextView = (TextView) findViewById(R.id.textView_movie_name);
        TextView mUserratingTextView = (TextView) findViewById(R.id.textView_user_ratings);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.textView_release_date);
        TextView mPlotSynopsis = (TextView) findViewById(R.id.textView_movie_plot);

        // set the data to UI components
        Picasso.with(DetailsActivity.this).load(movieBasicDetails.getMoviePoster()).into(mMoviePosterImageView);
        mTitleTextView.setText(movieBasicDetails.getMovieTitle());
        String userRatings = getResources().getString(R.string.info_ratings)+movieBasicDetails.getMovieUserRating();
        mUserratingTextView.setText(userRatings);
        String releaseDate = getResources().getString(R.string.info_release_date)+ movieBasicDetails.getMovieReleaseDate();
        mReleaseDateTextView.setText(releaseDate);
        String moviePlot = getResources().getString(R.string.info_plot_synopsis)+ "\n"+ movieBasicDetails.getMoviePlot();
        mPlotSynopsis.setText(moviePlot);
    }
}
