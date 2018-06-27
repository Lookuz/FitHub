package com.fithub.codekienmee.fithub;

import java.util.Date;

/**
 * Class that holds the data necessary to display the contents of a post in ForumFragment.
 */
public class FitPost {

    private String title;
    private String content;
    private String author;
    private int numLikes;
    private int numDislikes;
    private Date date;

    public FitPost(String title, String content,
                   String author, int numLikes, int numDislikes, Date date) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumDislikes() {
        return numDislikes;
    }

    public Date getDate() {
        return date;
    }
}