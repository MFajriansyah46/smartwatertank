package com.bangraja.smartwatertank.controller;

import com.bangraja.smartwatertank.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NotificationController {
    private final NotificationModel nm;
    private final HashMap<String, Long> lastNotificationTimes = new HashMap<>();

    public NotificationController() {
        nm = new NotificationModel();
    }

    public void sendNotification(String message) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastNotificationTimes.getOrDefault(message, 0L);

        // Kalau belum 10 detik sejak notifikasi yang sama terakhir dikirim, skip
        if (currentTime - lastTime < 10000) {
            return;
        }

        lastNotificationTimes.put(message, currentTime);

        DatabaseReference notifRef = nm.getNotifRef().push();

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        data.put("pesan", message);
        data.put("jam", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        data.put("tanggal", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        data.put("timestamp", currentTime); // <- penting untuk hapus otomatis

        notifRef.setValue(data);
    }
}
