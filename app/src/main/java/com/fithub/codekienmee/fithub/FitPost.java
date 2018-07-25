package com.fithub.codekienmee.fithub;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Class that holds the data necessary to display the contents of a post in ForumFragment.
 */
public class FitPost implements PostCallBack, Serializable {

    private String title;
    private String content;
    private String author;
    private int numLikes;
    private int numDislikes;
    private String date;
    private List<FitPost> comments;
    private Map<String, Boolean> likeMap; // HashMap that tracks if user has liked/disliked post.
    private String postKey;

    public FitPost() {
        // Default Constructor
    }

    public FitPost(String title, String content,
                   String author, int numLikes, int numDislikes, String date) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
        this.date = date;
        this.comments = new ArrayList<>();
    }

    public FitPost(String title, String content, String author, String date) {
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
        return this.date;
    }

    public List<FitPost> getComments() {
        return comments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public void setNumDislikes(int numDislikes) {
        this.numDislikes = numDislikes;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComments(List<FitPost> comments) {
        this.comments = comments;
    }

    public Map<String, Boolean> getLikeMap() {
        return likeMap;
    }

    public void setLikeMap(Map<String, Boolean> likeMap) {
        this.likeMap = likeMap;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    private void addComment(FitPost post) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }

        this.comments.add(post);
    }

    /**
     * Evaluates what happens when user likes post.
     */
    public void evalLike(FitUser user) {
        if (this.likeMap == null) {
            this.likeMap = new HashMap<>();
        }

        if (this.likeMap.containsKey(user.getUid()) &&
                this.likeMap.get(user.getUid()) != null) {
            if (this.likeMap.get(user.getUid())) { // Already liked
                Log.d("FitPost: ", "Already Liked");
                this.likeMap.remove((user.getUid()));
                this.numLikes--;
            } else { // Previously disliked
                Log.d("FitPost: ", "Previously disliked");
                this.likeMap.put(user.getUid(), true);
                this.numLikes++;
                this.numDislikes--;
            }
        } else {
            Log.d("FitPost: ", "Never Liked");
            this.likeMap.put(user.getUid(), true);
            this.numLikes++;
        }
    }

    /**
     * Evaluates what happens when user dislikes post.
     */
    public void evalDislike(FitUser user) {
        if (this.likeMap == null) {
            this.likeMap = new HashMap<>();
        }

        if (this.likeMap.containsKey(user.getUid()) &&
                this.likeMap.get(user.getUid()) != null) {
            if (!this.likeMap.get(user.getUid())) { // Already disliked
                Log.d("FitPost: ", "Already Disliked");
                this.likeMap.remove((user.getUid()));
                this.numDislikes--;
            } else { // Previously Liked
                Log.d("FitPost: ", "Previously Liked");
                this.likeMap.put(user.getUid(), false);
                this.numLikes--;
                this.numDislikes++;
            }
        } else {
            Log.d("FitPost: ", "Never Disliked");
            this.likeMap.put(user.getUid(), false);
            this.numDislikes++;
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
