package com.vhp.moviesstage1.retrofit;


import android.content.Context;

import com.vhp.moviesstage1.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by ashwani on 28/09/15.
 */
public final class RetrofitAPICaller {


    private static RetrofitAPICaller sRetrofitApiCaller;

    private static Context sContext;

    private MoviesApiService moviesApiService;

    private RetrofitAPICaller() {
        setupRetroAdapter();
    }

    public static RetrofitAPICaller getInstance(Context context) {
        sContext = context;
        if (sRetrofitApiCaller == null) {
            sRetrofitApiCaller = new RetrofitAPICaller();
        }
        return sRetrofitApiCaller;
    }

    private void setupRetroAdapter() {

        Retrofit digestRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        moviesApiService = digestRetrofit.create(MoviesApiService.class);
    }

    public MoviesApiService getMoviesAPIs() {
        return moviesApiService;
    }

}
