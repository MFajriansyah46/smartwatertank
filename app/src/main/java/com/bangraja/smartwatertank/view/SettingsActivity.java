package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.controller.NotificationController;

public class SettingsActivity extends AppCompatActivity {
    private TextView emailTextView;
    private Switch bukaKeranOtomatis;
    private Button logoutBtn;

    private AuthController ac;
    private CommandController cc;
    private NotificationController nc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inisialisasi controller
        ac = new AuthController();
        cc = new CommandController();
        nc = NotificationController.getInstance();

        // Hubungkan dengan layout
        emailTextView = findViewById(R.id.emailView);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Tampilkan email
        ac.displayUserEmail(emailTextView);

        // Setup switch
        cc.autoSwitch(bukaKeranOtomatis, this);

        // Tambahkan listener untuk notif ketika user nyalakan/matikan mode otomatis
        bukaKeranOtomatis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                nc.sendNotification("Mode otomatis aktif: Saat ini anda sedang berada dalam mode pengisian otomatis.");
            } else {
                nc.sendNotification("Mode otomatis dimatikan: Anda telah keluar dari mode pengisian otomatis.");
            }
        });

        // Logout button
        logoutBtn.setOnClickListener(view -> ac.logout(SettingsActivity.this));
    }
}