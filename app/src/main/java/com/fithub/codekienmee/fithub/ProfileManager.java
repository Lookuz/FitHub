package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that manages updating the timeline for user profiles.
 */
public class ProfileManager {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * Method to register log for user that just joined FitHub.
     */
    public static void signedUp(Context context, FitUser user) {
        String message = user.getName() + " " + context.getString(R.string.profile_signed_up);
        user.setTimeline(new ArrayList<String>());
        user.updateTimeline(message);
    }

    /**
     * Method to update user timeline when user creates a post.
     */
    public static void createPost(Context context, FitUser user, FitPost post) {
        String message = user.getName() + " " + context.getString(R.string.profile_created_post) + " "
                + post.getTitle();
        user.updateTimeline(message);
//        user.addPost(post);
        user.addPostKey(post.getPostKey());
//        user.updateStatistics();
    }

    /**
     * Method to update user profile after favouriting post.
     */
    public static void favouritePost(Context context, final FitUser user, final FitPost post) {

        String message = user.getName() + " " + context.getString(R.string.profile_favourited_post) + " "
                + post.getTitle();

        if (user.getFavouritePostKeys() == null ||
                !user.getFavouritePostKeys().contains(post.getPostKey())) {
            ((MainPageActivity) context).makeSnackBar(
                    post.getTitle() + " " + context.getString(R.string.profile_manager_favourited));
            user.favouritePostKey(post.getPostKey());
        } else {
            ((MainPageActivity) context).makeSnackBar(
                    post.getTitle() + " " + context.getString(R.string.profile_manager_unfavourited));
            user.unfavouritePostKey(post.getPostKey());
        }

        user.favouritePost(post);
        user.updateTimeline(message);
    }

    /**
     * Method to update user location upon signing into a FitLocation.
     */
    public static void addLocation(Context context, FitUser user, FitLocation location) {
        String message = user.getName() + " " + context.getString(R.string.profile_check_in) + " "
                + location.getLocationName();
        user.updateTimeline(message);
    }

    /**
     * Method to add location into user's list of favourites.
     */
    public static void favouriteLocation(Context context, FitUser user, FitLocation location) {

        if (user.getFavouriteLocationsKey() == null ||
                !user.getFavouriteLocationsKey().contains(location.getLocationKey())) {
            ((MainPageActivity) context).makeSnackBar(location.getLocationName() + " " +
                    context.getString(R.string.profile_manager_favourited));
            user.favouriteLocationKey(location.getLocationKey());
        } else {
            ((MainPageActivity) context).makeSnackBar(location.getLocationName() + " " +
                    context.getString(R.string.profile_manager_unfavourited));
            user.unfavouriteLocationKey(location.getLocationKey());
        }

        user.favouriteLocation(location);
    }

    // TODO: Migrate loading of favourites to profile manager?

    /**
     * Method to update any changes made in updating user profile.
     */
    public static void updateProfile(FitUser user, String name, String bio) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        user.setName(name);
        user.setBio(bio);

        databaseReference.setValue(user);
    }
}
