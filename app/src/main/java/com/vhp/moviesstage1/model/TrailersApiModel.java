
package com.vhp.moviesstage1.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersApiModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MoviesTrailersResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MoviesTrailersResult> getResults() {
        return results;
    }

    public void setResults(List<MoviesTrailersResult> results) {
        this.results = results;
    }

}
