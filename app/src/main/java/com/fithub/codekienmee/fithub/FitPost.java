package com.fithub.codekienmee.fithub;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that holds the data necessary to display the contents of a post in ForumFragment.
 */
public class FitPost implements Comparable<FitPost> {

    private String title;
    private String content;
    private String author;
    private int numLikes;
    private int numDislikes;
    private Date date;
    private List<FitPost> comments;

    public FitPost(String title, String content,
                   String author, int numLikes, int numDislikes, Date date) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
        this.date = date;
        this.comments = new ArrayList<>();
    }

    public FitPost(String title, String content, String author, Date date) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.numLikes = 0;
        this.numDislikes = 0;
        this.date = date;
        this.comments = new ArrayList<>();
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

    public List<FitPost> getComments() {
        return comments;
    }

    public void addComment(FitPost post) {
        this.comments.add(post);
    }

    @Override
    public int compareTo(@NonNull FitPost o) {
        if (o.date.after(this.date)) {
            return -1;
        } else if (o.date.before(this.date)) {
            return 1;
        } else {
            return 0;
        }
    }
}
