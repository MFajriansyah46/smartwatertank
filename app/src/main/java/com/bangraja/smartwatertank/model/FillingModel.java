package com.bangraja.smartwatertank.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class FillingModel {

    private final CollectionReference fillingRef;

    public FillingModel() {
        fillingRef = FirebaseFirestore.getInstance().collection("tb_pengisian");

    }

    public CollectionReference getFillingRef() {
        return fillingRef;
    }
}
