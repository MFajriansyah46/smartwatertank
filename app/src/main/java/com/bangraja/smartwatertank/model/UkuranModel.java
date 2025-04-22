package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UkuranModel {

    private final DatabaseReference um;

    public UkuranModel() {

        um = FirebaseDatabase.getInstance().getReference("tb_ukuran");
    }

    public DatabaseReference getUkuranRef() {
        return um;
    }

}
