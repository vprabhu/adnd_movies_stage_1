package com.vhp.moviesstage1.model;

/**
 * Created by root on 9/7/17.
 */

public class MovieTrailers {

    private String youtubeId;
    private String trailerName;

    public MovieTrailers(String youtubeId, String name) {
        this.youtubeId = youtubeId;
        this.trailerName = name;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public String gettrailerName() {
        return trailerName;
    }
}
