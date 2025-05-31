package com.bangraja.smartwatertank.controller;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.model.CommandModel;
import com.bangraja.smartwatertank.model.NotificationModel;
import com.bangraja.smartwatertank.view.custom.RiverEffect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class CommandController {
    private final CommandModel cm;
    private final Context context;
    ValueEventListener Listener;

    public CommandController(CommandModel cm, Context context) {
        this.cm = cm;
        this.context = context;
    }

    // Mengatur Switch untuk buka keran otomatis
    public void autoSwitch(Switch bukaKeranOtomatis) {

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

            // ✅ Kirim notifikasi lokal dan ke Firebase
            NotificationController nc = new NotificationController(new NotificationModel());
            if (isChecked) {
                nc.sendNotification(context, "Mode otomatis diaktifkan: Saat ini anda sedang berada dalam pengisian mode otomatis");
            } else {
                nc.sendNotification(context, "Mode otomatis dimatikan: Anda telah keluar dari mode pengisian otomatis");
            }
        });
    }

    public void manualSwitch(Switch bukaKeran, TextView estimasiView, View riverEffect, LinearLayout switchContainer) {
        RiverEffect re = new RiverEffect(riverEffect);
        Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);
                boolean isOn = statusKeran != null && statusKeran;
                bukaKeran.setChecked(isOn);

                if (isOn) {
                    re.startRiverEffect();
                    estimasiView.setVisibility(View.VISIBLE);
                    switchContainer.setBackground(ContextCompat.getDrawable(switchContainer.getContext(), R.drawable.active_rounded_box));
                } else {
                    re.stopRiverEffect();
                    estimasiView.setVisibility(View.GONE);
                    switchContainer.setBackground(ContextCompat.getDrawable(switchContainer.getContext(), R.drawable.rounded_box));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        };

        cm.getCommandRef().addValueEventListener(Listener);

        final boolean[] fromUser = {false};

        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!fromUser[0]) return;

            cm.getCommandRef().child("keran").setValue(isChecked);

            NotificationController nc = new NotificationController(new NotificationModel());
            if (isChecked) {
                nc.sendNotification(context, "Keran dibuka");
            } else {
                nc.sendNotification(context, "Keran ditutup");
            }

            fromUser[0] = false;
        });

        bukaKeran.setOnTouchListener((v, event) -> {
            fromUser[0] = true;
            return false;
        });
    }

    public void cleanup() {
        if (Listener != null) {
            cm.getCommandRef().removeEventListener(Listener);
            Listener = null;
        }
    }
}
