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

    public CommandController(CommandModel cm) {
        this.cm = cm;
    }

    // Mengatur Switch untuk buka keran otomatis
    public void autoSwitch(Switch bukaKeranOtomatis) {

        // Mendengarkan perubahan nilai "otomatis" di Firebase
        cm.getCommandRef().child("otomatis").addValueEventListener(new ValueEventListener() {
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
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            cm.getCommandRef().child("otomatis").setValue(isChecked);
            cm.getCommandRef().child("operator").setValue(email);
        });
    }

    public void manualSwitch(Switch bukaKeran) {
        ValueEventListener perintahListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);
                bukaKeran.setChecked(statusKeran != null && statusKeran);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors.
            }
        };
        cm.addCommandListener(perintahListener);

        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> cm.updateKeranStatus(isChecked));
    }

    public void cleanup() {
        cm.removeCommandListener(null);
    }
}
