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
    private static final String TAG = "NotificationController";
    private static NotificationController instance;
    private final FirebaseFirestore db;

    private Boolean lastKeran = null;  // Menyimpan status terakhir keran
    private Boolean lastOtomatis = null;  // Menyimpan status terakhir otomatis

    private NotificationController() {
        db = FirebaseFirestore.getInstance();
    }

    public static NotificationController getInstance() {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }

    // Menghandle perubahan status Switch Manual
    public void handleManualSwitchChange(boolean isOn) {
        if (lastKeran != null && lastKeran == isOn) return;  // Menghindari notifikasi ganda
        lastKeran = isOn;

        String pesan = isOn ? "Keran dibuka" : "Keran ditutup";
        Log.d(TAG, "handleManualSwitchChange dipanggil, pesan: " + pesan);
        sendNotification(pesan);
    }

    // Menghandle perubahan status Switch Otomatis
    public void handleAutoSwitchChange(boolean isOn) {
        if (lastOtomatis != null && lastOtomatis == isOn) return;  // Menghindari notifikasi ganda
        lastOtomatis = isOn;

        String pesan = isOn ?
                "Mode otomatis diaktifkan: Saat ini anda sedang berada dalam pengisian mode otomatis" :
                "Mode otomatis dimatikan: Anda telah keluar dari mode pengisian otomatis";
        Log.d(TAG, "handleAutoSwitchChange dipanggil, pesan: " + pesan);
        sendNotification(pesan);
    }

    // Mengirimkan notifikasi
    public void sendNotification(String pesan) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e(TAG, "User belum login, tidak bisa kirim notifikasi");
            return;
        }

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d(TAG, "Mengirim notifikasi untuk user: " + email);

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

        db.collection("tb_notifikasi")
                .add(data)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Notifikasi berhasil dikirim: " + pesan))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Gagal kirim notifikasi: " + e.getMessage(), e));
    }

    // Membersihkan listener agar tidak terjadi double notification
    public void cleanup() {
        lastKeran = null;
        lastOtomatis = null;
    }
}
