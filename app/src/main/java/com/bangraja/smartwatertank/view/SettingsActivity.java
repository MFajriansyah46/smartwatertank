package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.controller.NotificationController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsActivity extends AppCompatActivity {
    private EditText profileNameEditText;
    private ImageView editNameIcon;
    private TextView emailTextView;
    private Switch bukaKeranOtomatis;
    private Button logoutBtn;

    private AuthController ac;
    private CommandController cc;
    private NotificationController nc;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ac = new AuthController();
        cc = new CommandController();
        nc = NotificationController.getInstance();

        profileNameEditText = findViewById(R.id.profileName);
        editNameIcon = findViewById(R.id.editNameIcon);
        emailTextView = findViewById(R.id.emailView);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Setup awal: disable edit nama
        profileNameEditText.setEnabled(false);
        profileNameEditText.setFocusable(false);
        profileNameEditText.setCursorVisible(false);

        // Tampilkan email pengguna
        ac.displayUserEmail(emailTextView);

        // Tampilkan nama pengguna dari Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            profileNameEditText.setText(user.getDisplayName());
        }

        // Tombol edit nama
        editNameIcon.setOnClickListener(v -> {
            if (!isEditing) {
                // Masuk mode edit
                isEditing = true;
                profileNameEditText.setEnabled(true);
                profileNameEditText.setFocusableInTouchMode(true);
                profileNameEditText.setCursorVisible(true);
                profileNameEditText.requestFocus();
                editNameIcon.setImageResource(R.drawable.ic_done);
            } else {
                // Simpan ke Firebase
                String newName = profileNameEditText.getText().toString().trim();
                if (!newName.isEmpty() && user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Nama diperbarui", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Gagal memperbarui nama", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Kembali ke mode view
                isEditing = false;
                profileNameEditText.setEnabled(false);
                profileNameEditText.setFocusable(false);
                profileNameEditText.setCursorVisible(false);
                editNameIcon.setImageResource(R.drawable.ic_edit);
            }
        });

        // Ambil status switch dari SharedPreferences
        boolean savedAutoMode = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("auto_mode", false);
        bukaKeranOtomatis.setChecked(savedAutoMode);

        // Listener untuk switch
        bukaKeranOtomatis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("auto_mode", isChecked)
                    .apply();

            if (isChecked) {
                nc.sendNotification("Mode otomatis aktif: Saat ini anda sedang berada dalam mode pengisian otomatis.");
            } else {
                nc.sendNotification("Mode otomatis dimatikan: Anda telah keluar dari mode pengisian otomatis.");
            }

            cc.setAutoMode(isChecked);
        });

        // Tombol logout
        logoutBtn.setOnClickListener(view -> ac.logout(SettingsActivity.this));
    }
}