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
import java.util.List;
import java.util.Map;

/**
 * Class that displays details of a user's profile.
 */
public class FitUser implements Serializable {

    private String name;
    private String email;
    private final String uid;

    private List<FitPost> favouritePosts;
    private List<FitLocation> favouriteLocations;
    private Map<String, Boolean> userSettings;

    public FitUser() {
        //Default Constructor
        this.uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();
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
            Log.d("User: ", "Post " + post.getTitle() + " added.");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FitUser.this);
            return true;
        }
    }
}
