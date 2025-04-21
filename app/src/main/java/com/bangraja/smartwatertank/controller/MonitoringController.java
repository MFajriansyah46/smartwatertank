package com.bangraja.smartwatertank.controller;

import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.google.firebase.firestore.DocumentSnapshot;
import com.github.mikephil.charting.data.Entry;
import com.bangraja.smartwatertank.model.TransmiterModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class MonitoringController {
    private final TransmiterModel tm;

    private Double heightValue, waterVolumeValue, maxVolumeValue;
    private Long pressureValue;

    public MonitoringController() {
        tm = new TransmiterModel();
    }

    public void realtimeData(TextView pressure, TextView height, TextView waterVolume,ProgressBar progressVolume, TextView progressPercent) {
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

    public List<Entry> statisticData(List<DocumentSnapshot> documents, String filter, List<String> labels) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Collections.sort(documents, (d1, d2) -> d1.getTimestamp("timestamp").toDate().compareTo(d2.getTimestamp("timestamp").toDate()));

        // Variabel untuk pengelompokkan data setiap 15 menit
        long intervalMillis = 15 * 60 * 1000; // 15 menit
        long startTime = -1;
        int count = 0;
        float totalVolume = 0;

        for (int i = 0; i < documents.size(); i++) {
            DocumentSnapshot doc = documents.get(i);
            Double volume = doc.getDouble("water_volume");
            Date date = doc.getTimestamp("timestamp").toDate();

            if (volume != null && date != null) {
                long currentTime = date.getTime();

                // Jika belum ada data, set waktu mulai
                if (startTime == -1) {
                    startTime = currentTime;
                }

                // Periksa apakah data sudah melewati periode 15 menit
                if ((currentTime - startTime) >= 15 * 60 * 1000) {
                    // Tambahkan entry untuk periode yang sudah selesai
                    if (count > 0) {
                        entries.add(new Entry(entries.size(), totalVolume / count));
                        labels.add(sdf.format(new Date(startTime)));
                    }

                    // Reset penghitungan untuk periode berikutnya
                    startTime = currentTime;
                    count = 0;
                    totalVolume = 0;
                }

                // Tambahkan volume ke total volume dalam periode tersebut
                totalVolume += volume.floatValue();
                count++;
            }
        }

        // Jika masih ada data yang belum terkelompokkan (misalnya terakhir)
        if (count > 0) {
            entries.add(new Entry(entries.size(), totalVolume / count));
            labels.add(sdf.format(new Date(startTime)));
        }

        return entries;
    }

    public void historyData() {

    }

    public void cleanup() {
        tm.removeTransmiterListener(null);
    }
}
