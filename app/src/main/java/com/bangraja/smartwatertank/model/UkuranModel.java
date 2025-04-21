package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UkuranModel {

    private final FirebaseDatabase fb;
    private final DatabaseReference um;

    public UkuranModel() {

        fb = FirebaseDatabase.getInstance();
        um = fb.getReference("tb_ukuran");
    }

    public DatabaseReference getRef() {
        return um;
    }
}
