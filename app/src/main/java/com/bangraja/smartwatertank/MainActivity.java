package com.bangraja.smartwatertank;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private TextView pressure, height, water_volume;
    private Switch bukaKeran;
    private DatabaseReference transmiter, perintah;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inisialisasi bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_statistik) {
                Toast.makeText(this, "Statistik", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_notifikasi) {
                Toast.makeText(this, "Notifikasi", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        pressure = findViewById(R.id.pressure);
        height = findViewById(R.id.height);
        water_volume = findViewById(R.id.water_volume);
        bukaKeran = findViewById(R.id.bukaKeran);

        transmiter = FirebaseDatabase.getInstance().getReference("transmiter");
        perintah = FirebaseDatabase.getInstance().getReference("perintah");

        transmiter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    pressure.setText("No data");
                    height.setText("No data");
                    water_volume.setText("No data");
                    return;
                }

                Double heightValue = snapshot.child("height").getValue(Double.class);
                Long pressureValue = snapshot.child("pressure").getValue(Long.class);
                Double waterVolumeValue = snapshot.child("water_volume").getValue(Double.class);

                pressure.setText(pressureValue != null ?        "Tekanan\t\t \t" + pressureValue + " Pa" : "N/A");
                height.setText(heightValue != null ?            "Ketinggian\t \t" + String.format("%.5f", heightValue) + " m" : "N/A");
                water_volume.setText(waterVolumeValue != null ? "Volume\t\t\t \t" + String.format("%.5f", waterVolumeValue) + " L" : "N/A");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pressure.setText("Error loading data");
                height.setText("Error loading data");
                water_volume.setText("Error loading data");
            }
        });

        perintah.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);
                    if (statusKeran != null) {
                        bukaKeran.setChecked(statusKeran);
                    }
                } else {
                    bukaKeran.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal memuat status keran", Toast.LENGTH_SHORT).show();
            }
        });

        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            perintah.child("keran").setValue(isChecked);
        });
    }
}
