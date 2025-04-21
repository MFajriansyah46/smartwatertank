package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationModel {
    private final DatabaseReference notifRef;

    public NotificationModel() {
        notifRef = FirebaseDatabase.getInstance().getReference("notifikasi");
    }

    public DatabaseReference getNotifRef() {
        return notifRef;
    }
}
