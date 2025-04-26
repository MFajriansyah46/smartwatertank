package com.bangraja.smartwatertank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommandModel {
    private final DatabaseReference commandRef;

    public CommandModel() {
        // Inisialisasi referensi ke node "perintah" di Firebase Realtime Database
        commandRef = FirebaseDatabase.getInstance().getReference("perintah");
    }

    public DatabaseReference getCommandRef() {
        return commandRef;
    }

}
