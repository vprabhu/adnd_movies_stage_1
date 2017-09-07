package com.vhp.moviesstage1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vhp.moviesstage1.R;
import com.vhp.moviesstage1.model.MovieReviews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/23/17.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MoviesReviewsAdapterViewHolder> {

    private List<MovieReviews> moviesList = new ArrayList<>();

    public MovieReviewsAdapter(List<MovieReviews> moviesListParam ) {
        moviesList = moviesListParam;
    }

    public class MoviesReviewsAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mMoviesReviewsAuthorTextView;
        public final TextView mMoviesReviewsContentTextView;

        public MoviesReviewsAdapterViewHolder(View view) {
            super(view);
            mMoviesReviewsAuthorTextView  = (TextView) view.findViewById(R.id.textView_movie_review_author);
            mMoviesReviewsContentTextView  = (TextView) view.findViewById(R.id.textView_movie_review_content);
        }

        // loads the data into the UI Components
        void bind(int listIndex){
            mMoviesReviewsAuthorTextView.setText(moviesList.get(listIndex).getAuthor());
            mMoviesReviewsContentTextView.setText(moviesList.get(listIndex).getContent());
        }

    }


    @Override
    public MoviesReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.layout_reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflates the desired layout
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MoviesReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesReviewsAdapterViewHolder holder, int position) {
        // binds the data to UI
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
