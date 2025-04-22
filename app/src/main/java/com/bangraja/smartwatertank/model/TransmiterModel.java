package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransmiterModel {

    private final DatabaseReference transmiterRef;
    public TransmiterModel() {
        transmiterRef = FirebaseDatabase.getInstance().getReference("transmiter");
    }

    public DatabaseReference getTransmiterRef() {
        return transmiterRef;
    }

    public void addTransmiterListener(ValueEventListener listener) {
        transmiterRef.addValueEventListener(listener);
    }

    public void removeTransmiterListener(ValueEventListener listener) {
        transmiterRef.removeEventListener(listener);
    }
}
