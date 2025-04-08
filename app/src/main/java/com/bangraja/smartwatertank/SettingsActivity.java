package com.bangraja.smartwatertank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private Button logoutBtn;
    private TextView emailTextView;

    private Switch bukaKeranOtomatis;

    private DatabaseReference perintah;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.emailView);
        logoutBtn = findViewById(R.id.logoutBtn);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);

        perintah = FirebaseDatabase.getInstance().getReference("perintah");

        bukaKeranOtomatis(bukaKeranOtomatis,perintah);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            emailTextView.setText(currentUser.getEmail());
        } else {
            emailTextView.setText("Tidak ada akun login");
        }

        logoutBtn.setOnClickListener(view -> {
            logout();
        });
    }

    private void bukaKeranOtomatis(Switch bukaKeranOtomatis,DatabaseReference perintah) {
        perintah.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean status = snapshot.child("otomatis").getValue(Boolean.class);
                    bukaKeranOtomatis.setChecked(status != null && status);
                } else {
                    bukaKeranOtomatis.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Gagal memuat status keran", Toast.LENGTH_SHORT).show();
            }
        });

        bukaKeranOtomatis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();

                perintah.child("otomatis").setValue(isChecked);
                perintah.child("operator").setValue(email);

                Toast.makeText(SettingsActivity.this, "Status otomatis diperbarui", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Pengguna tidak login", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}