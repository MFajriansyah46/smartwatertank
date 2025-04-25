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

        // Tampilkan email user
        ac.displayUserEmail(emailTextView);

        // Ambil status terakhir dari SharedPreferences
        boolean savedAutoMode = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("auto_mode", false);
        bukaKeranOtomatis.setChecked(savedAutoMode);

        // Listener untuk perubahan switch
        bukaKeranOtomatis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simpan status ke SharedPreferences
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("auto_mode", isChecked)
                    .apply();

            if (isChecked) {
                nc.sendNotification("Mode otomatis aktif: Saat ini anda sedang berada dalam mode pengisian otomatis.");
            } else {
                nc.sendNotification("Mode otomatis dimatikan: Anda telah keluar dari mode pengisian otomatis.");
            }

            // Kirim perintah ke mikrokontroler atau Firebase
            cc.setAutoMode(isChecked); // fungsi tambahan opsional
        });

        // Tombol logout
        logoutBtn.setOnClickListener(view -> ac.logout(SettingsActivity.this));
    }
}

