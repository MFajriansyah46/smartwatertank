package com.bangraja.smartwatertank.controller;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.bangraja.smartwatertank.Main;
import com.bangraja.smartwatertank.R;
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
    private static final String CHANNEL_ID = "smartwater_notif_channel";

    public NotificationController(NotificationModel nm) {
        this.nm = nm;
    }

    public void sendNotification(Context context, String pesan) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Waktu lokal
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
                .addOnSuccessListener(documentReference -> {
                    Log.d("Notif", "Berhasil kirim notifikasi");
                    showLocalNotification(context, email, pesan);
                })
                .addOnFailureListener(e -> Log.e("Notif", "Gagal kirim notifikasi"));
    }

    private void showLocalNotification(Context context, String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission belum diberikan, jangan tampilkan notifikasi
                Log.w("Notif", "Permission POST_NOTIFICATIONS belum diberikan");
                return;
            }
        }

        createNotificationChannel(context);

        Intent intent = new Intent(context, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Smart Water Notification";
            String description = "Channel untuk notifikasi aplikasi Smart Water";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
