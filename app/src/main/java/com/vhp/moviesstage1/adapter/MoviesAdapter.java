package com.vhp.moviesstage1.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vhp.moviesstage1.R;
import com.vhp.moviesstage1.data.MoviesContract;
import com.vhp.moviesstage1.model.MoviesInfo;

/**
 * Created by root on 7/23/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private Cursor mCursor;
    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(MoviesAdapterOnClickHandler moviesAdapterOnClickHandler) {
        mClickHandler = moviesAdapterOnClickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(MoviesInfo moviesInfoData);
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
            mCursor.moveToPosition(listIndex);
            int movieTitleIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int moviePosterIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER);
            String movieTitle = mCursor.getString(movieTitleIndex);
            String moviePoster = mCursor.getString(moviePosterIndex);

            mMoviesTitleTextView.setText(movieTitle);
            Picasso.with(itemView.getContext()).load(moviePoster).into(mMoviesImageView);
        }

        @Override
        public void onClick(View view) {
            int pos  = mCursor.getPosition();
            Log.d("Adapter", "onClick: " + pos);
            mCursor.moveToPosition(getAdapterPosition());
            int movieIdIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIES_ID);
            int movieTitleIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int moviePlotIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_PLOT);
            int movieReleaseDateIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
            int moviePosterIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER);
            int movieRatingsIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_USER_RATING);

            String movieId = mCursor.getString(movieIdIndex);
            String moviePoster = mCursor.getString(moviePosterIndex);
            String movieTitle = mCursor.getString(movieTitleIndex);
            String moviePlot = mCursor.getString(moviePlotIndex);
            String movieReleaseDate = mCursor.getString(movieReleaseDateIndex);
            String movieRatings = mCursor.getString(movieRatingsIndex);

            MoviesInfo moviesInfo = new MoviesInfo(
                    movieId ,
                    movieTitle ,
                    moviePoster ,
                    moviePlot ,
                    movieRatings ,
                    movieReleaseDate
            );

            mClickHandler.onClick(moviesInfo);
        }
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
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
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

}
