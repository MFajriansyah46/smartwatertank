package com.bangraja.smartwatertank.controller;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.bangraja.smartwatertank.model.CommandModel;
import com.bangraja.smartwatertank.model.NotificationModel;
import com.bangraja.smartwatertank.view.custom.RiverEffect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.bangraja.smartwatertank.R;

public class CommandController {
    private final CommandModel cm;

    ValueEventListener Listener;

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

            // Notifikasi untuk mode otomatis
            NotificationController nc = new NotificationController(new NotificationModel());
            if (isChecked) {
                nc.sendNotification("Mode otomatis diaktifkan: Saat ini anda sedang berada dalam pengisian mode otomatis");
            } else {
                nc.sendNotification("Mode otomatis dimatikan: Anda telah keluar dari mode pengisian otomatis");
            }
        });
    }

    public void manualSwitch(Switch bukaKeran, View riverEffect, LinearLayout switchContainer) {
        RiverEffect re = new RiverEffect(riverEffect);
        Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);
                boolean isOn = statusKeran != null && statusKeran;
                bukaKeran.setChecked(isOn);

                if (isOn) {
                    re.startRiverEffect();
                    switchContainer.setBackground(ContextCompat.getDrawable(switchContainer.getContext(), R.drawable.active_rounded_box));
                } else {
                    re.stopRiverEffect();
                    switchContainer.setBackground(ContextCompat.getDrawable(switchContainer.getContext(), R.drawable.rounded_box));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Error handling
            }
        };

        cm.getCommandRef().addValueEventListener(Listener);

        // Flag untuk mendeteksi apakah perubahan datang dari pengguna
        final boolean[] fromUser = {false};

        // Menangani perubahan status manual dari switch
        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!fromUser[0]) return; // Jika perubahan bukan berasal dari user, jangan kirim notifikasi

            cm.getCommandRef().child("keran").setValue(isChecked);

            // Mengirim notifikasi hanya ketika status berubah oleh pengguna
            NotificationController nc = new NotificationController(new NotificationModel());
            if (isChecked) {
                nc.sendNotification("Keran dibuka");
            } else {
                nc.sendNotification("Keran ditutup");
            }

            fromUser[0] = false; // Reset setelah aksi user
        });

        // Deteksi perubahan dari pengguna
        bukaKeran.setOnTouchListener((v, event) -> {
            fromUser[0] = true;
            return false;
        });
    }

    public void cleanup() {
        if (Listener != null) {
            cm.getCommandRef().removeEventListener(Listener);
            Listener = null;  // Menghapus listener setelah tidak dibutuhkan lagi
        }
    }

}
