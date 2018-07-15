package com.fithub.codekienmee.fithub;

import com.google.firebase.auth.FirebaseUser;

/**
 * Class that displays details of a user's profile.
 */
public class FitProfile {

    private FirebaseUser user; // user account in firebase.

    private String name;
    private String email;
    private final String uid; //


    public FitProfile(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.uid = user.getUid();
    }
}
