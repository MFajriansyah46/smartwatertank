package com.bangraja.smartwatertank.controller;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bangraja.smartwatertank.model.FillingModel;
import com.bangraja.smartwatertank.model.TransmiterModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    private static Double heightValue, waterVolumeValue, maxVolumeValue;
    private Long pressureValue;
    private ValueEventListener transmiterListener;
    private final TransmiterModel tm;
    private NotificationController notificationController;

    // Variabel untuk menyimpan persentase terakhir yang sudah diberi notifikasi
    private int lastNotificationPercent = -1;

    public DashboardController(TransmiterModel tm) {
        this.tm = tm;
        this.notificationController = new NotificationController(new com.bangraja.smartwatertank.model.NotificationModel());
    }

    // Terima Context untuk dipakai di notificationController
    public void realtimeData(TextView pressure, TextView height, TextView waterVolume,
                             ProgressBar progressVolume, TextView progressPercent, TextView estimasiView,
                             Context context) {

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

                int percent = 0;
                if (waterVolumeValue != null && maxVolumeValue != null && maxVolumeValue > 0) {
                    percent = Math.min(100, (int) Math.round((waterVolumeValue / maxVolumeValue) * 100));
                }
                progressVolume.setProgress(percent);
                progressPercent.setText(String.valueOf(percent));

                // **Cek dan kirim notifikasi "Sebentar lagi air penuh" sekali saja saat persen >= 75**
                if (percent >= 90 && lastNotificationPercent < 90) {
                    notificationController.sendNotification(context,  "Sebentar lagi air penuh");
                }
                lastNotificationPercent = percent;

                // Panggil pengecekan notifikasi lainnya (jika ada)
                notificationController.checkWaterLevelAndNotify(percent, context);

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
                .whereGreaterThan("terisi", 0)
                .whereGreaterThanOrEqualTo("waktu", 1)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .limit(25)
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
                        if (timePrediction > 0) {
                            estimasiView.setText(timePrediction + " menit hingga penuh");
                        } else {
                            estimasiView.setText("Tangki penuh");
                        }
                    } else {
                        estimasiView.setVisibility(View.GONE);
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
