package com.bangraja.smartwatertank.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationModel {
    private final FirebaseAuth auth;

    public AuthenticationModel() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
