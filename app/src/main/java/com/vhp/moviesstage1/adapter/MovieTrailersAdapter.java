package com.vhp.moviesstage1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vhp.moviesstage1.R;
import com.vhp.moviesstage1.model.MovieTrailers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/23/17.
 */

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MoviesTrailersAdapterViewHolder> {

    private List<MovieTrailers> moviesList = new ArrayList<>();
    private final MoviesTrailersAdapterOnClickHandler mClickHandler;

    public MovieTrailersAdapter(List<MovieTrailers> moviesListParam, MoviesTrailersAdapterOnClickHandler mClickHandler) {
        moviesList = moviesListParam;
        this.mClickHandler = mClickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesTrailersAdapterOnClickHandler {
        void onTrailerClick(String youtubeLink);
    }

    public class MoviesTrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mTrailerNameTextView;

        public MoviesTrailersAdapterViewHolder(View view) {
            super(view);
            mTrailerNameTextView = (TextView) view.findViewById(R.id.textView_trailer_name);
            view.setOnClickListener(this);
        }

        // loads the data into the UI Components
        void bind(int listIndex){
            mTrailerNameTextView.setText(moviesList.get(listIndex).gettrailerName());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String youtubeId = moviesList.get(adapterPosition).getYoutubeId();
            mClickHandler.onTrailerClick(youtubeId);
        }
    }


    @Override
    public MoviesTrailersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.layout_trailers_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflates the desired layout
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MoviesTrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesTrailersAdapterViewHolder holder, int position) {
        // binds the data to UI
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
