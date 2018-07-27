package com.fithub.codekienmee.fithub;

import java.io.Serializable;
import java.util.ArrayList;
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
    private String bio;

    private List<String> timeline;
//    private List<FitPost> posts;
    private List<String> postsKeys; // List that stores keys of posted content.
    private List<FitPost> favouritePosts;
    private List<String> favouritePostKeys; // List that stores keys of favourited posts.
    private List<FitLocation> favouriteLocations;
    private List<String> favouriteLocationsKey;
    private Map<String, Boolean> userSettings;

    public FitUser() {
        //Default Constructor
        this.favouritePosts = new ArrayList<>();
        this.favouriteLocations = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getTotalLikes() {
        return this.totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public List<String> getPostsKeys() {
        return postsKeys;
    }

    public void setPostsKeys(List<String> postsKeys) {
        this.postsKeys = postsKeys;
    }

    public List<String> getFavouritePostKeys() {
        return favouritePostKeys;
    }

    public void setFavouritePostKeys(List<String> favouritePostKeys) {
        this.favouritePostKeys = favouritePostKeys;
    }

    public List<FitPost> getFavouritePosts() {
        return this.favouritePosts;
    }

    public List<FitLocation> getFavouriteLocations() {
        return favouriteLocations;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Boolean> getUserSettings() {
        return userSettings;
    }

    public List<String> getFavouriteLocationsKey() {
        return favouriteLocationsKey;
    }

    public void setFavouriteLocationsKey(List<String> favouriteLocationsKey) {
        this.favouriteLocationsKey = favouriteLocationsKey;
    }

    /**
     * Method to update user timeline.
     */
    public void updateTimeline(String message) {
        if (message == null || message.equals("")) {
            return;
        }

        if (this.timeline.contains(message)) {
            this.timeline.remove(message);
        } else {
            this.timeline.add(0, message);
        }
    }

    /**
     * Method to add a post to User's list of favourite posts.
     */
    public void favouritePost(FitPost post) {
        if (this.favouritePosts == null ) {
            this.favouritePosts = new ArrayList<>();
        }

        if (this.favouritePosts.contains(post)) {
            this.favouritePosts.remove(post);
        } else {
            this.favouritePosts.add(post);
        }
    }

    /**
     * Adds the key of the favourited post to user list.
     */
    public void favouritePostKey(String key) {
        if (this.favouritePostKeys == null) {
            this.favouritePostKeys = new ArrayList<>();
        }

        this.favouritePostKeys.add(0, key);
    }

    public void unfavouritePostKey(String key) {
        if (this.favouritePostKeys != null) {
            this.favouritePostKeys.remove(key);
        }
    }
//
//    /**
//     * Method that adds a post to the list of posts by the user.
//     */
//    public void addPost(FitPost post) {
//        if (this.posts == null) {
//            this.posts = new ArrayList<>();
//        }
//
//        // Add to front.
//        this.posts.add(0, post);
//    }

    /**
     * Method to add post key to user's list of posted keys
     */
    public void addPostKey(String postKey) {
        if (this.postsKeys == null) {
            this.postsKeys = new ArrayList<>();
        }

        // Add to front.
        this.postsKeys.add(0, postKey);
    }

    public void removePostKey(String postKey) {
        if (this.postsKeys != null) {
            this.postsKeys.remove(postKey);
        }
    }

    /**
     * Method to favourite a location.
     */
    public boolean favouriteLocation(FitLocation location) {
        if (this.favouriteLocations.contains(location)) {
            this.favouriteLocations.remove(location);
            return false;
        } else {
            this.favouriteLocations.add(location);
            return true;
        }
    }

    /**
     * Method to add a location key to user's list of favourite locations keys.
     * @param locationKey
     */
    public void favouriteLocationKey(String locationKey) {
        if (this.favouriteLocationsKey == null) {
            this.favouriteLocationsKey = new ArrayList<>();
        }
        this.favouriteLocationsKey.add(0, locationKey);
    }

    public void unfavouriteLocationKey(String locationKey) {
        if (this.favouriteLocationsKey != null) {
            this.favouriteLocationsKey.remove(locationKey);
        }
    }

//    /**
//     * Method to update user's numbers and stats.
//     */
//    public void updateStatistics() {
//        if (this.posts != null) {
//            int totalLikes = 0;
//            for (FitPost post : posts) {
//                totalLikes += post.getNumLikes();
//            }
//            this.totalLikes = totalLikes;
//            this.totalShares = this.posts.size();
//        } else {
//            this.totalShares = 0;
//            this.totalLikes = 0;
//        }
//    }
}