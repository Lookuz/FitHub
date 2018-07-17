package com.fithub.codekienmee.fithub;

import java.util.HashMap;
import java.util.List;

/**
 * Class that displays details of a user's profile.
 */
public class FitUser {

    private String name;
    private String email;
    private List<FitPost> favouritePosts;
    private List<FitLocation> favouriteLocations;
    private HashMap<String, Boolean> userSettings;

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

    public void setUserSettings(HashMap<String, Boolean> userSettings) {
        this.userSettings = userSettings;
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

    public HashMap<String, Boolean> getUserSettings() {
        return userSettings;
    }
}
