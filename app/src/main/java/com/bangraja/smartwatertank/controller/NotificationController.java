package com.bangraja.smartwatertank.controller;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationController {
    private static NotificationController instance;
    private final FirebaseFirestore db;

    private NotificationController() {
        db = FirebaseFirestore.getInstance();
    }

    public static NotificationController getInstance() {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }

    public void sendNotification(String pesan) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Timestamp lokal (untuk ditampilkan)
        Date now = new Date();
        String jam = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
        String tanggal = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(now);

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("pesan", pesan);
        data.put("jam", jam);
        data.put("tanggal", tanggal);
        data.put("timestamp", Timestamp.now()); // ✅ Gunakan Firestore timestamp
        data.put("isRead", false);

        db.collection("tb_notifikasi")
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d("Notif", "Berhasil kirim notifikasi"))
                .addOnFailureListener(e -> Log.e("Notif", "Gagal kirim notifikasi", e));
    }
}

