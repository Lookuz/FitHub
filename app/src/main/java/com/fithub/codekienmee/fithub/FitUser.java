package com.fithub.codekienmee.fithub;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class that displays details of a user's profile.
 */
public class FitUser implements Serializable {

    private String name;
    private String email;
    private int totalLikes;
    private int totalShares;
    private final String uid;

    private List<String> timeline;
    private List<FitPost> posts;
    private List<FitPost> postsKeys;
    private List<FitPost> favouritePosts;
    private List<String> favouritePostKeys;
    private List<FitLocation> favouriteLocations;
    private Map<String, Boolean> userSettings;

    public FitUser() {
        //Default Constructor
        this.uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();
        this.favouritePosts = new ArrayList<>();
        this.favouriteLocations = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFavouritePosts(List<FitPost> favouritePosts) {
        this.favouritePosts = favouritePosts;
    }

    public void setFavouriteLocations(List<FitLocation> favouriteLocations) {
        this.favouriteLocations = favouriteLocations;
    }

    public void setUserSettings(Map<String, Boolean> userSettings) {
        this.userSettings = userSettings;
    }

    public List<String> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<String> timeline) {
        this.timeline = timeline;
    }

    public List<FitPost> getPosts() {
        return posts;
    }

    public void setPosts(List<FitPost> posts) {
        this.posts = posts;
    }

    public int getTotalLikes() {
        return this.totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public List<FitPost> getPostsKeys() {
        return postsKeys;
    }

    public void setPostsKeys(List<FitPost> postsKeys) {
        this.postsKeys = postsKeys;
    }

    public List<String> getFavouritePostKeys() {
        return favouritePostKeys;
    }

    public void setFavouritePostKeys(List<String> favouritePostKeys) {
        this.favouritePostKeys = favouritePostKeys;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<FitPost> getFavouritePosts() {
        return favouritePosts;
    }

    public List<FitLocation> getFavouriteLocations() {
        return favouriteLocations;
    }

    public Map<String, Boolean> getUserSettings() {
        return userSettings;
    }

    /**
     * Method to add a post to User's list of favourite posts.
     */
    public boolean favouritePost(FitPost post) {
        if (this.favouritePosts == null ) {
            this.favouritePosts = new ArrayList<>();
        }

        if (this.favouritePosts.contains(post)) {
            return false;
        } else {
            this.favouritePosts.add(post);
            return true;
        }
    }

    /**
     * Adds the key of the favourited post to user list.
     */
    public void favouritePostKey(String key) {
        if (this.favouritePostKeys == null) {
            this.favouritePostKeys = new ArrayList<>();
        }

        this.favouritePostKeys.add(key);
    }

    /**
     * Method that adds a post to the list of posts by the user.
     */
    public void addPost(FitPost post) {
        if (this.posts == null) {
            this.posts = new ArrayList<>();
        }

        // Add to front.
        this.posts.add(0, post);
    }

    /**
     * Method to update user's numbers and stats.
     */
    public void updateStatistics() {
        if (this.posts != null) {
            int totalLikes = 0;
            for (FitPost post : posts) {
                totalLikes += post.getNumLikes();
            }
            this.totalLikes = totalLikes;
            this.totalShares = this.posts.size();
        } else {
            this.totalShares = 0;
            this.totalLikes = 0;
        }
    }
}
