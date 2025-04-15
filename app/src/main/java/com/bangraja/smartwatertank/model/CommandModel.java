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

    // Mendapatkan referensi ke node "perintah"
    public DatabaseReference getCommandRef() {
        return commandRef;
    }

    public void addCommandListener(ValueEventListener listener) {
        commandRef.addValueEventListener(listener);
    }

    public void removeCommandListener(ValueEventListener listener) {
        commandRef.removeEventListener(listener);
    }

    public void updateKeranStatus(boolean isChecked) {
        commandRef.child("keran").setValue(isChecked);
    }
}
