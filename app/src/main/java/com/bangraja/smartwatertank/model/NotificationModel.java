package com.bangraja.smartwatertank.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationModel {
    private final CollectionReference notifRef;

    public NotificationModel() {
        notifRef = FirebaseFirestore.getInstance().collection("tb_notifikasi");
    }

    public CollectionReference getNotifRef() {
        return notifRef;
    }
}
