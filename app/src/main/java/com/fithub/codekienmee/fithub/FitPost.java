package com.fithub.codekienmee.fithub;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<String, Object> likeMap; // HashMap that tracks if user has liked/disliked post.
    private String postKey;
    private Boolean hasMap;

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

    public Map<String, Object> getLikeMap() {
        return likeMap;
    }

    public void setLikeMap(Map<String, Object> likeMap) {
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

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        if (this.likeMap.containsKey(uid) &&
                this.likeMap.get(uid) != null) {
            if ((Boolean) this.likeMap.get(uid)) { // Already liked
                Log.d("FitPost: ", "Already Liked");
                this.likeMap.put(uid, null);
                this.numLikes--;
            } else { // Previously disliked
                Log.d("FitPost: ", "Previously disliked");
                this.likeMap.put(uid, true);
                this.numLikes++;
                this.numDislikes--;
            }
        } else {
            Log.d("FitPost: ", "Never Liked");
            this.likeMap.put(uid, true);
            this.numLikes++;
        }

        updateLikes();
    }

    /**
     * Evaluates what happens when user dislikes post.
     */
    public void evalDislike(FitUser user) {

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        if (this.likeMap.containsKey(uid) &&
                this.likeMap.get(uid) != null) {
            if (!(Boolean) this.likeMap.get(uid)) { // Already disliked
                Log.d("FitPost: ", "Already Disliked");
                this.likeMap.put(uid, null);
                this.numDislikes--;
            } else { // Previously Liked
                Log.d("FitPost: ", "Previously Liked");
                this.likeMap.put(uid, false);
                this.numLikes--;
                this.numDislikes++;
            }
        } else {
            Log.d("FitPost: ", "Never Disliked");
            this.likeMap.put(uid, false);
            this.numDislikes++;
        }

        updateLikes();
    }

    /**
     * Method to add new comment to this post as it's callback.
     */
    @Override
    public void onCallBack(FitPost post) {
        this.addComment(post);
    }

    public void retrieveLikes() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("FitPosts");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey)
                        .child("likeMap").exists()) {
                    GenericTypeIndicator<Map<String, Object>> genericTypeIndicator =
                            new GenericTypeIndicator<Map<String, Object>>() {};
                    setLikeMap(dataSnapshot.child(postKey).child("likeMap").getValue(genericTypeIndicator));
                } else {
                    setLikeMap(new HashMap<String, Object>());
                }

                try {
                    setNumLikes(dataSnapshot.child(postKey).child("numLikes").getValue(Integer.class));
                    setNumDislikes(dataSnapshot.child(postKey).child("numDislikes").getValue(Integer.class));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateLikes() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("FitPosts")
                .child(postKey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("likeMap").exists()) {
                    databaseReference.child("likeMap").updateChildren(likeMap);
                } else {
                    databaseReference.child("likeMap").setValue(likeMap);
                }

                databaseReference.child("numDislikes").setValue(numDislikes);
                databaseReference.child("numLikes").setValue(numLikes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
