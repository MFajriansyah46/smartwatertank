package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransmiterModel {

    private final DatabaseReference tm;
    public TransmiterModel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tm = database.getReference("transmiter");
    }

    public DatabaseReference getRef() {
        return tm;
    }

    public void addTransmiterListener(ValueEventListener listener) {
        tm.addValueEventListener(listener);
    }

    public void removeTransmiterListener(ValueEventListener listener) {
        tm.removeEventListener(listener);
    }
}
