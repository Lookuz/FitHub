package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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
        user.addPost(post);
        user.addPostKey(post.getPostKey());
        user.updateStatistics();
    }

    /**
     * Method to update user profile after favouriting post.
     * TODO: create method for unfavouriting posts.
     */
    public static void favouritePost(Context context, final FitUser user, final FitPost post) {

        String message = user.getName() + " " + context.getString(R.string.profile_favourited_post) + " "
                + post.getTitle();

        if (user.favouritePost(post)) { // If post successfully added
            ((MainPageActivity) context).makeSnackBar(
                    post.getTitle() + " " + context.getString(R.string.profile_manager_favourited));
            user.favouritePostKey(post.getPostKey());
        } else {
            ((MainPageActivity) context).makeSnackBar(
                    post.getTitle() + " " + context.getString(R.string.profile_manager_unfavourited));
            user.favouritePostKey(post.getPostKey());
            user.unfavouritePostKey(post.getPostKey());
        }

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
        if (user.favouriteLocation(location)) {
            ((MainPageActivity) context).makeSnackBar(location.getLocationName() + " " +
                    context.getString(R.string.profile_manager_favourited));
            user.favouriteLocationKey(location.getLocationKey());
        } else {
            ((MainPageActivity) context).makeSnackBar(location.getLocationName() + " " +
                    context.getString(R.string.profile_manager_unfavourited));
            user.unfavouriteLocationKey(location.getLocationKey());
        }
    }

    /**
     * Method to load all of the users posts and favourited posts on start.
     * TODO: Consider separate thread for this action?
     */
    public static void loadPosts(final FitUser user) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("FitPosts");

        // TODO: Run async.
        if (user.getFavouritePostKeys() != null || user.getPostsKeys() != null) {

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (user.getPostsKeys() != null && user.getPostsKeys().contains(ds.getKey())) {
                                    Log.d("Adding Post: ", ds.getKey());
                                    user.addPost(ds.getValue(FitPost.class));
                                }

                                if (user.getFavouritePostKeys() != null &&
                                        user.getFavouritePostKeys().contains(ds.getKey())) {
                                    Log.d("Favouriting Post: ", ds.getKey());
                                    user.favouritePost(ds.getValue(FitPost.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    /**
     * Method to load user's list of favourite locations from the location keys.
     */
    public static void loadLocations(final FitUser user) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("FitLocations");

        if (user.getFavouriteLocationsKey() != null) {

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (user.getFavouriteLocationsKey().contains(ds.getKey())) {
                                    user.favouriteLocation(ds.getValue(FitLocation.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }
}
