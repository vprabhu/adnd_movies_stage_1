package com.vhp.moviesstage1.retrofit;


import com.vhp.moviesstage1.model.Movies;
import com.vhp.moviesstage1.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vicky on 08/12/15.
 */
public interface MoviesApiService {

   @GET(Constants.MOVIES_POPULAR)
   Call<Movies> getPopularMoviesListAPI(@Query (Constants.API_KEY_LABEL) String apikey);

   @GET(Constants.MOVIES_TOP_RATED)
   Call<Movies> getTopRatedMoviesListAPI(@Query (Constants.API_KEY_LABEL) String apikey);

}
