package com.fithub.codekienmee.fithub;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that holds the data necessary to display the contents of a post in ForumFragment.
 */
public class FitPost implements Comparable<FitPost>, PostCallBack {

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

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("EEE d MMMM yyyy hh:mm aaa");
        return dateFormat.format(this.date);
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

    /**
     * Method to add new comment to this post as it's callback.
     */
    @Override
    public void onCallBack(FitPost post) {
        this.addComment(post);
    }
}
