package com.bangraja.smartwatertank.controller;

import android.app.Activity;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bangraja.smartwatertank.model.CommandModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class CommandController {
    private final CommandModel cm;
    private final FirebaseAuth auth;

    public CommandController() {
        // Inisialisasi CommandModel dan FirebaseAuth
        cm = new CommandModel();
        auth = FirebaseAuth.getInstance();
    }

    // Mengatur Switch untuk buka keran otomatis
    public void autoSwitch(Switch bukaKeranOtomatis, Activity activity) {
        DatabaseReference getCommandRef = cm.getCommandRef();

        // Mendengarkan perubahan nilai "otomatis" di Firebase
        getCommandRef.child("otomatis").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean status = snapshot.getValue(Boolean.class);
                bukaKeranOtomatis.setChecked(status != null && status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Menangani perubahan status Switch oleh pengguna
        bukaKeranOtomatis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();
                getCommandRef.child("otomatis").setValue(isChecked);
                getCommandRef.child("operator").setValue(email);
                Toast.makeText(activity, "Status otomatis diperbarui", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Pengguna tidak login", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
