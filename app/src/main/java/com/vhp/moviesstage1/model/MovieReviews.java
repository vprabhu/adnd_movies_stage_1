package com.vhp.moviesstage1.model;

/**
 * Created by root on 9/7/17.
 */

public class MovieReviews {

    private String author;
    private String content;

    public MovieReviews(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
