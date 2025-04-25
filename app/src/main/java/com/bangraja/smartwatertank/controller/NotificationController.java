package com.bangraja.smartwatertank.controller;

import com.bangraja.smartwatertank.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NotificationController {
    private static NotificationController instance;
    private final NotificationModel nm;
    private final HashMap<String, Long> lastNotificationTimes = new HashMap<>();

    public static NotificationController getInstance() {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }

    private NotificationController() {
        nm = new NotificationModel();
    }

    public void sendNotification(String message) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastNotificationTimes.getOrDefault(message, 0L);

        if (currentTime - lastTime < 10000) {
            return;
        }

        lastNotificationTimes.put(message, currentTime);

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        data.put("pesan", message);
        data.put("jam", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        data.put("tanggal", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        data.put("timestamp", currentTime);

        nm.getNotifRef().add(data); // <- Firestore pakai add() bukan push()
    }
}
