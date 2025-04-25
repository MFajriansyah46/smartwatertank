package com.bangraja.smartwatertank.controller;

import android.app.Activity;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bangraja.smartwatertank.model.CommandModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class CommandController {
    private final CommandModel cm;
    private ValueEventListener manualListener;

    public CommandController() {
        cm = new CommandModel();
    }

    public void autoSwitch(Switch bukaKeranOtomatis, Activity activity) {
        cm.getCommandRef().child("otomatis").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean status = snapshot.getValue(Boolean.class);
                bukaKeranOtomatis.setChecked(status != null && status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        bukaKeranOtomatis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            cm.getCommandRef().child("otomatis").setValue(isChecked);
            cm.getCommandRef().child("operator").setValue(email);
            Toast.makeText(activity, "Status otomatis diperbarui", Toast.LENGTH_SHORT).show();
        });
    }

    public void setAutoMode(boolean isActive) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        cm.getCommandRef().child("otomatis").setValue(isActive);
        cm.getCommandRef().child("operator").setValue(email);
    }

    public void manualSwitch(Switch bukaKeran) {
        NotificationController nc = NotificationController.getInstance();

        if (manualListener != null) {
            cm.getCommandRef().removeEventListener(manualListener);
        }

        final boolean[] fromUser = {false}; // Flag untuk mendeteksi asal perubahan

        manualListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);

                // 🚫 Jangan kirim notifikasi saat status keran diupdate dari Firebase
                fromUser[0] = false;

                // Set switch tanpa trigger listener
                bukaKeran.setOnCheckedChangeListener(null);
                bukaKeran.setChecked(statusKeran != null && statusKeran);
                bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!fromUser[0]) return; // Bukan dari user, skip notifikasi

                    cm.updateKeranStatus(isChecked);
                    if (isChecked) {
                        nc.sendNotification("Keran dibuka");
                    } else {
                        nc.sendNotification("Keran ditutup");
                    }

                    fromUser[0] = false; // Reset setelah aksi user
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        // Pasang listener Firebase
        cm.getCommandRef().addValueEventListener(manualListener);

        // Deteksi user saat tekan switch secara manual
        bukaKeran.setOnTouchListener((v, event) -> {
            fromUser[0] = true;
            return false;
        });
    }



    private void setManualSwitchListener(Switch bukaKeran, NotificationController nc) {
        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cm.updateKeranStatus(isChecked);
            if (isChecked) {
                nc.sendNotification("Keran dibuka");
            } else {
                nc.sendNotification("Keran ditutup");
            }
        });
    }

    // ✅ Cleanup supaya listener ga menumpuk
    public void cleanup() {
        if (manualListener != null) {
            cm.getCommandRef().removeEventListener(manualListener);
            manualListener = null;
        }
    }
}

