package com.bangraja.smartwatertank.controller;

import android.widget.Switch;
import android.widget.TextView;
import com.bangraja.smartwatertank.model.DashboardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DashboardController {
    private final DashboardModel dm;

    public DashboardController() {
        dm = new DashboardModel();
    }

    public void setupTransmiterListener(TextView pressure, TextView height, TextView waterVolume) {
        ValueEventListener transmiterListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    pressure.setText("No data");
                    height.setText("No data");
                    waterVolume.setText("No data");
                    return;
                }

                Double heightValue = snapshot.child("height").getValue(Double.class);
                Long pressureValue = snapshot.child("pressure").getValue(Long.class);
                Double waterVolumeValue = snapshot.child("water_volume").getValue(Double.class);

                pressure.setText(pressureValue != null ? pressureValue.toString() : "N/A");
                height.setText(heightValue != null ? String.format("%.2f", heightValue) : "N/A");
                waterVolume.setText(waterVolumeValue != null ? String.format("%.2f", waterVolumeValue) : "N/A");
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

    public void setupPerintahListener(Switch bukaKeran) {
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
        dm.addCommandListener(perintahListener);

        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> dm.updateKeranStatus(isChecked));
    }

    public void cleanup() {
        dm.removeTransmiterListener(null);
        dm.removeCommandListener(null);
    }
}
