package com.bangraja.smartwatertank.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthModel {
    private final FirebaseAuth auth;
    private final CollectionReference profileRef;
    public AuthModel() {
        auth = FirebaseAuth.getInstance();
        profileRef = FirebaseFirestore.getInstance().collection("tb_profile");
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public CollectionReference getProfile() {
        return profileRef;
    }
}
