package com.vhp.moviesstage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vhp.moviesstage1.model.Movies;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // getting the movie data from budle which is passed from MainActivity
        Bundle data = getIntent().getExtras();
        Movies movies = data.getParcelable("Movies");

        // set activity title as selected movie name
        getSupportActionBar().setTitle(movies.getMovieTitle());
        // set back button
        getSupportActionBar().setHomeButtonEnabled(true);

        // UI Typecasting
        ImageView mMoviePosterImageView = (ImageView) findViewById(R.id.imageView_detail);
        TextView mTitleTextView = (TextView) findViewById(R.id.textView_movie_name);
        TextView mUserratingTextView = (TextView) findViewById(R.id.textView_user_ratings);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.textView_release_date);
        TextView mPlotSynopsis = (TextView) findViewById(R.id.textView_movie_plot);

        // set the data to UI components
        Picasso.with(DetailsActivity.this).load(movies.getMoviePoster()).into(mMoviePosterImageView);
        mTitleTextView.setText(movies.getMovieTitle());
        String userRatings = getResources().getString(R.string.info_ratings)+movies.getMovieUserRating();
        mUserratingTextView.setText(userRatings);
        String releaseDate = getResources().getString(R.string.info_release_date)+ movies.getMovieReleaseDate();
        mReleaseDateTextView.setText(releaseDate);
        mPlotSynopsis.setText(movies.getMoviePlot());
    }
}
