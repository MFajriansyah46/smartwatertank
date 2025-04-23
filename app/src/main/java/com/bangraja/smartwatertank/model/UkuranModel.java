package com.bangraja.smartwatertank.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UkuranModel {

    private final CollectionReference um;

    public UkuranModel() {

        um = FirebaseFirestore.getInstance().collection("tb_ukuran");
    }

    public CollectionReference getUkuranRef() {
        return um;
    }
}
