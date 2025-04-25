package com.bangraja.smartwatertank.controller;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.bangraja.smartwatertank.model.CommandModel;
import com.bangraja.smartwatertank.view.custom.RiverEffect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

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
        });
    }

    public void manualSwitch(Switch bukaKeran, View riverEffect) {

        RiverEffect re = new RiverEffect(riverEffect);
        Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);
                boolean isOn = statusKeran != null && statusKeran;
                bukaKeran.setChecked(isOn);

                if (isOn) {
                    re.startRiverEffect();
                } else {
                    re.stopRiverEffect();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Error handling
            }
        };

        cm.getCommandRef().addValueEventListener(Listener);

        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cm.getCommandRef().child("keran").setValue(isChecked);
            if (isChecked) {
                re.startRiverEffect();
            } else {
                re.stopRiverEffect();
            }
        });
    }
}
