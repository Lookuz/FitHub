package com.fithub.codekienmee.fithub;

import com.google.firebase.auth.FirebaseUser;

/**
 * Class that displays details of a user's profile.
 */
public class FitUser {

    private FirebaseUser user; // user account in firebase.

    private String name;
    private String email;
    private final String uid;


    public FitUser(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.uid = user.getUid();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
