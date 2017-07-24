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

    private Context mContext;
    private List<Movies> moviesList = new ArrayList<>();

    public MoviesAdapter(Context ctxParam , List<Movies> moviesListParam) {
        moviesList = moviesListParam;
        mContext = ctxParam;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mMoviesTitleTextView;
        public final ImageView mMoviesImageView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mMoviesTitleTextView  = (TextView) view.findViewById(R.id.textView_title);
            mMoviesImageView  = (ImageView) view.findViewById(R.id.imageView_movie_item);
        }

        void bind(int listIndex){
            mMoviesTitleTextView.setText(moviesList.get(listIndex).getMovieTitle());
//            mMoviesImageView.setText(String.valueOf(listIndex));
            Picasso.with(mContext).load("https://image.tmdb.org/t/p/w500" +
                    moviesList.get(listIndex).getMoviePoster()).into(mMoviesImageView);
//            https://image.tmdb.org/t/p/w500
        }
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.layout_movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MoviesAdapterViewHolder moviesViewHolder = new MoviesAdapterViewHolder(view);

        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
