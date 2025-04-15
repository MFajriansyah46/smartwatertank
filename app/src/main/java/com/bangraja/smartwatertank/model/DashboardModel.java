package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardModel {
    private final DatabaseReference transmiter, perintah;

    public DashboardModel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        transmiter = database.getReference("transmiter");
        perintah = database.getReference("perintah");
    }

    public void addTransmiterListener(ValueEventListener listener) {
        transmiter.addValueEventListener(listener);
    }

    public void removeTransmiterListener(ValueEventListener listener) {
        transmiter.removeEventListener(listener);
    }
}