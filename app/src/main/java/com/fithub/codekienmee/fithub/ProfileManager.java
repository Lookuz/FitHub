package com.fithub.codekienmee.fithub;

import android.content.Context;

import java.util.ArrayList;

/**
 * Class that manages updating the timeline for user profiles.
 */
public class ProfileManager {

    /**
     * Method to register log for user that just joined FitHub.
     */
    public static void signedUp(Context context, FitUser user) {
        String message = user.getName() + " " + context.getString(R.string.profile_signed_up);
        user.setTimeline(new ArrayList<String>());
        user.getTimeline().add(0, message);
    }

    /**
     * Method to update user timeline when user creates a post.
     */
    public static void createPost(Context context, FitUser user, FitPost post) {
        String message = user.getName() + " " + context.getString(R.string.profile_created_post) + " "
                + post.getTitle();
        user.getTimeline().add(0, message);
        user.getPosts().add(0, post);
    }

    /**
     * Method to update user location uponing signing into a FitLocation.
     */
    public static void addLocation(Context context, FitUser user, FitLocation location) {
        String message = user.getName() + " " + context.getString(R.string.profile_check_in) + " "
                + location.getLocationName();
        user.getTimeline().add(0, message);
    }
}
