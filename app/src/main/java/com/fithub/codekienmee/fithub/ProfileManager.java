package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        user.updateStatistics();
    }

    /**
     * Method to update user profile after favouriting post.
     * TODO: create method for unfavouriting posts.
     */
    public static void favouritePost(Context context, final FitUser user, final FitPost post) {

        if (user.favouritePost(post)) { // If post successfully added to
            Query query = FirebaseDatabase.getInstance().getReference("FitPosts")
                    .orderByChild("title")
                    .equalTo(post.getTitle());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getValue(FitPost.class).getTitle().equals(post.getTitle())) {
                                // TODO: Refine key fetching.
                                Log.d("Post exists: ", "Favouriting posts");
                                user.favouritePostKey(ds.getKey());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String message = user.getName() + " " + context.getString(R.string.profile_favourited_post) + " "
                    + post.getTitle();
            user.updateTimeline(message);
        }
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
     * Method to load all of the users posts and favourited posts on start.
     * TODO: Consider separate thread for this action?
     */
    public static void loadPosts(final FitUser user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("FitPosts");

        if (user.getFavouritePostKeys() != null || user.getPostsKeys() != null) {
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
    }
}
