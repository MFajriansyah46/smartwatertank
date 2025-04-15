package com.bangraja.smartwatertank.controller;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import com.bangraja.smartwatertank.model.DashboardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DashboardController {
    private final DashboardModel dm;

    private Double heightValue, waterVolumeValue;
    private Long pressureValue;

    public DashboardController() {
        dm = new DashboardModel();
    }

    public void setupTransmiterListener(TextView pressure, TextView height, TextView waterVolume,ProgressBar progressVolume, TextView progressPercent) {
        ValueEventListener transmiterListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    pressure.setText("No data");
                    height.setText("No data");
                    waterVolume.setText("No data");
                    return;
                }

                heightValue = snapshot.child("height").getValue(Double.class);
                pressureValue = snapshot.child("pressure").getValue(Long.class);
                waterVolumeValue = snapshot.child("water_volume").getValue(Double.class);

                pressure.setText(pressureValue != null ? pressureValue.toString() : "N/A");
                height.setText(heightValue != null ? String.format("%.2f", heightValue) : "N/A");
                waterVolume.setText(waterVolumeValue != null ? String.format("%.2f", waterVolumeValue) : "N/A");

                int percent = Math.min(100, (int) Math.round((waterVolumeValue / 1200.0) * 100));
                progressVolume.setProgress(percent);
                progressPercent.setText(String.valueOf(percent));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                pressure.setText("Error loading data");
                height.setText("Error loading data");
                waterVolume.setText("Error loading data");
            }
        };
        dm.addTransmiterListener(transmiterListener);
    }

    public void cleanup() {
        dm.removeTransmiterListener(null);
    }
}
