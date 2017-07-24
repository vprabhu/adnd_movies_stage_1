package com.vhp.moviesstage1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vhp.moviesstage1.R;
import com.vhp.moviesstage1.model.Movies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/23/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movies> moviesList = new ArrayList<>();
    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(List<Movies> moviesListParam , MoviesAdapterOnClickHandler moviesAdapterOnClickHandler) {
        moviesList = moviesListParam;
        mClickHandler = moviesAdapterOnClickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(Movies moviesData);
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mMoviesTitleTextView;
        public final ImageView mMoviesImageView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mMoviesTitleTextView  = (TextView) view.findViewById(R.id.textView_title);
            mMoviesImageView  = (ImageView) view.findViewById(R.id.imageView_movie_item);
            view.setOnClickListener(this);
        }

        // loads the data into the UI Components
        void bind(int listIndex){
            mMoviesTitleTextView.setText(moviesList.get(listIndex).getMovieTitle());
            Picasso.with(itemView.getContext()).load("https://image.tmdb.org/t/p/w500" +
                    moviesList.get(listIndex).getMoviePoster()).into(mMoviesImageView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movies movieData = moviesList.get(adapterPosition);
            mClickHandler.onClick(movieData);
        }
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.layout_movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflates the desired layout
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        // binds the data to UI
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
