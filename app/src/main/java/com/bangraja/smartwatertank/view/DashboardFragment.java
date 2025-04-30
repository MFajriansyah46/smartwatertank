package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.controller.MonitoringController;
import com.bangraja.smartwatertank.model.TransmiterModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DashboardController {
    private Double heightValue, waterVolumeValue, maxVolumeValue;
    private Long pressureValue;
    private final TransmiterModel tm;
    public DashboardController(TransmiterModel tm) {

        this.tm = tm;
    }

    public void realtimeData(TextView pressure, TextView height, TextView waterVolume, ProgressBar progressVolume, TextView progressPercent) {
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
                maxVolumeValue = snapshot.child("max_volume").getValue(Double.class);

                pressure.setText(pressureValue != null ? pressureValue.toString() : "N/A");
                height.setText(heightValue != null ? String.format("%.2f", heightValue) : "N/A");
                waterVolume.setText(waterVolumeValue != null ? String.format("%.2f", waterVolumeValue) : "N/A");

                int percent = Math.min(100, (int) Math.round((waterVolumeValue / maxVolumeValue) * 100));
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
        tm.addTransmiterListener(transmiterListener);
    }
}
}
