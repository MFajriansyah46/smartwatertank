package com.bangraja.smartwatertank.controller;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.bangraja.smartwatertank.model.FillingModel;
import com.bangraja.smartwatertank.model.TransmiterModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    private static Double heightValue, waterVolumeValue, maxVolumeValue;
    private Long pressureValue;
    private ValueEventListener transmiterListener;
    private final TransmiterModel tm;
    public DashboardController(TransmiterModel tm) {

        this.tm = tm;
    }

    public void realtimeData(TextView pressure, TextView height, TextView waterVolume, ProgressBar progressVolume, TextView progressPercent, TextView estimasiView) {
        transmiterListener = new ValueEventListener() {
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
                estimasiFull(estimasiView, new FillingModel());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                pressure.setText("Error loading data");
                height.setText("Error loading data");
                waterVolume.setText("Error loading data");
            }
        };
        tm.getTransmiterRef().addValueEventListener(transmiterListener);
    }
    private static void estimasiFull(TextView estimasiView, FillingModel fm) {

        fm.getFillingRef()
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(15)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Double> volumeList = new ArrayList<>();
                    List<Double> timeList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double volume = doc.getDouble("terisi");
                        Double time = doc.getDouble("waktu");

                        if (volume != null && time != null) {
                            volumeList.add(volume);
                            timeList.add(time);
                        }
                    }

                    if (!volumeList.isEmpty()) {
                        double yPrediksi = singleExponentialSmoothing(volumeList, timeList);
                        int timePrediction = (int) Math.round(yPrediksi);
                        if(timePrediction > 0) {
                            estimasiView.setText(timePrediction + " menit hingga penuh");
                        } else {
                            estimasiView.setText("Tangki penuh");
                        }
                    } else {
                        estimasiView.setText("Data kosong");
                    }
                })
                .addOnFailureListener(e -> {
                    estimasiView.setText("Gagal memuat data");
                });
    }

    private static Double singleExponentialSmoothing(List<Double> volumeList, List<Double> timeList) {
        if (volumeList.isEmpty() || timeList.isEmpty()) return 0.0;

        double alpha = 0.3;
        List<Double> volumeRateList = new ArrayList<>();
        for (int i = 0; i < volumeList.size(); i++) {
            double rate = volumeList.get(i) / timeList.get(i);
            volumeRateList.add(rate);
        }

        double st = volumeRateList.get(0);
        for (int i = 1; i < volumeRateList.size(); i++) {
            st = alpha * volumeRateList.get(i) + (1 - alpha) * st;
        }

        return (maxVolumeValue - waterVolumeValue) / st;
    }
} 
