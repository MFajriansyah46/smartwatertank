package com.bangraja.smartwatertank.controller;

import android.util.Log;
import com.bangraja.smartwatertank.model.NotificationModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationController {
    private final NotificationModel nm;

    public NotificationController(NotificationModel nm) {
        this.nm = nm;
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
        data.put("timestamp", Timestamp.now());
        data.put("isRead", false);

        nm.getNotifRef()
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d("Notif", "Berhasil kirim notifikasi"))
                .addOnFailureListener(e -> Log.e("Notif", "Gagal kirim notifikasi"));
}
}
