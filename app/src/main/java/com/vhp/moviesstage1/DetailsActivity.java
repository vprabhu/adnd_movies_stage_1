package com.vhp.moviesstage1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vhp.moviesstage1.adapter.MovieReviewsAdapter;
import com.vhp.moviesstage1.adapter.MovieTrailersAdapter;
import com.vhp.moviesstage1.model.MovieReviews;
import com.vhp.moviesstage1.model.MovieTrailers;
import com.vhp.moviesstage1.model.MoviesInfo;
import com.vhp.moviesstage1.model.ReviewApiModel;
import com.vhp.moviesstage1.model.TrailersApiModel;
import com.vhp.moviesstage1.retrofit.RetrofitAPICaller;
import com.vhp.moviesstage1.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements MovieTrailersAdapter.MoviesTrailersAdapterOnClickHandler {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private List<MovieReviews> movieReviewsList = new ArrayList<>();
    private List<MovieTrailers> movieTrailersList = new ArrayList<>();
    private MoviesInfo movieBasicDetails;
    private RecyclerView mReviewsRecyclerView , mTrailersRecyclerView;
    private String youtubeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_scrolling);

        // getting the movie data from budle which is passed from MainActivity
        Bundle data = getIntent().getExtras();
        movieBasicDetails = data.getParcelable("MoviesInfo");
        String movieId = movieBasicDetails.getMovieId();

//        // set activity title as selected movie name
//        getSupportActionBar().setTitle(movieBasicDetails.getMovieTitle());
//        // set back button
//        getSupportActionBar().setHomeButtonEnabled(true);

        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movie_reviews);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movie_trailers);
        View mRelatedInfoView = (View) findViewById(R.id.view_related_info);

//        makeMovieReviewsApiRequest(movieId);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checks the trailer is available , otherwise informs the user about it
                if(!youtubeId.isEmpty()){
                    Intent appIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:" + youtubeId));
                    startActivity(appIntent);
                }else {
                    Snackbar.make(
                            view,
                            getResources().getString(R.string.info_no_trailers),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // sets the movie basic details
        bindDataToUI();

        // fetches the movie reviews and trailers
        fetchMovieReviews(movieId);
        fetchMovieTrailers(movieId);
    }

    @Override
    public void onTrailerClick(String youtubeLink) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeLink));
        startActivity(appIntent);
    }

    /**
     * This method binds the data to the UI
     */
    private void  bindDataToUI(){
        // UI Typecasting
        ImageView mMoviePosterImageView = (ImageView) findViewById(R.id.imageView_movie_poster);
        TextView mTitleTextView = (TextView) findViewById(R.id.textView_movie_name);
        RatingBar mMovieRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.textView_release_date);
        TextView mPlotSynopsis = (TextView) findViewById(R.id.textView_plot);

        // set the data to UI components
        Picasso.with(DetailsActivity.this).load(movieBasicDetails.getMoviePoster()).into(mMoviePosterImageView);
        mTitleTextView.setText(movieBasicDetails.getMovieTitle());
        float userRatings = Float.parseFloat(movieBasicDetails.getMovieUserRating());
        mMovieRatingBar.setRating(userRatings/2);
        String releaseDate = movieBasicDetails.getMovieReleaseDate();
        mReleaseDateTextView.setText(releaseDate);
        String moviePlot = movieBasicDetails.getMoviePlot();
        mPlotSynopsis.setText(moviePlot);
    }

    /**
     * Fetches the movie reviews according to movieId and fills the recyclerView
     * @param movieId movies ID to fetch the movie reviews
     */
    private void fetchMovieReviews(final String movieId){
        Call<ReviewApiModel> moviesCall =  RetrofitAPICaller.getInstance(DetailsActivity.this)
                .getMoviesAPIs().getMovieReviewsListAPI(movieId , Constants.API_KEY);
        moviesCall.enqueue(new Callback<ReviewApiModel>() {
            @Override
            public void onResponse(Call<ReviewApiModel> call, Response<ReviewApiModel> response) {
                Log.d(TAG, "onResponse: " + response);
                int responseSize = response.body().getResults().size();
                for (int i = 0; i < responseSize; i++) {
                    String author = response.body().getResults().get(i).getAuthor();
                    String content = response.body().getResults().get(i).getContent();

                    MovieReviews movieReviews = new MovieReviews(author , content);
                    movieReviewsList.add(movieReviews);
                }

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
                mReviewsRecyclerView.setNestedScrollingEnabled(false);
                mTrailersRecyclerView.addItemDecoration(new DividerItemDecoration(
                        DetailsActivity.this,
                        DividerItemDecoration.VERTICAL));

                mTrailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onFailure(Call<ReviewApiModel> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t);

            }
        });
    }

    /**
     * Fetches the movie trailers according to movieId and fills the recyclerView
     * @param movieId movies ID to fetch the movie trailers
     */
    private void fetchMovieTrailers(final String movieId){
        Call<TrailersApiModel> moviesCall =  RetrofitAPICaller.getInstance(DetailsActivity.this)
                .getMoviesAPIs().getMovieTrailersListAPI(movieId , Constants.API_KEY);
        moviesCall.enqueue(new Callback<TrailersApiModel>() {
            @Override
            public void onResponse(Call<TrailersApiModel> call, Response<TrailersApiModel> response) {
                Log.d(TAG, "onResponse: " + response);
                int responseSize = response.body().getResults().size();
                for (int i = 0; i < responseSize; i++) {
                    String key = response.body().getResults().get(i).getKey();
                    String name = response.body().getResults().get(i).getName();

                    MovieTrailers movieTrailers = new MovieTrailers(key , name);
                    movieTrailersList.add(movieTrailers);
                }

                // take the first trailer and make it to play at FAB listener and remove it from
                // movie list
                if(movieTrailersList.size()>0){
                    youtubeId = movieTrailersList.get(0).getYoutubeId();
                    movieTrailersList.remove(0);
                    // Trailer Adapter initialisation
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
                    mTrailersRecyclerView.setNestedScrollingEnabled(false);
                    mTrailersRecyclerView.addItemDecoration(new DividerItemDecoration(
                            DetailsActivity.this,
                            DividerItemDecoration.VERTICAL));

                    mTrailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                }


            }

            @Override
            public void onFailure(Call<TrailersApiModel> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t);
            }
        });
    }
}
