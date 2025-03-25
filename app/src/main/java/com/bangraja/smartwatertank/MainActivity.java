package com.bangraja.smartwatertank;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private TextView pressure, height, water_volume;
    private Switch bukaKeran;
    private DatabaseReference sensor, keran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pressure = findViewById(R.id.pressure);
        height = findViewById(R.id.height);
        water_volume = findViewById(R.id.water_volume);
        bukaKeran = findViewById(R.id.bukaKeran);

        // Inisialisasi Firebase Database Reference
        sensor = FirebaseDatabase.getInstance().getReference("tb_ukuran");
        keran = FirebaseDatabase.getInstance().getReference("keran");

        // Ambil data dari database secara real-time
        sensor.addValueEventListener(new ValueEventListener() {
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

                pressure.setText(pressureValue != null ?        "Tekanan\t\t \t" + String.valueOf(pressureValue) + " Pa" : "N/A");
                height.setText(heightValue != null ?            "Ketinggian\t \t" + String.format("%.5f", heightValue) + " m" : "N/A");
                water_volume.setText(waterVolumeValue != null ? "Volume\t\t\t \t" + String.format("%.5f", waterVolumeValue) + " m^3" : "N/A");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pressure.setText("Error loading data");
                height.setText("Error loading data");
                water_volume.setText("Error loading data");
            }
        });

        // Baca status keran dari Firebase
        keran.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean statusKeran = snapshot.getValue(Boolean.class);
                if (statusKeran != null) {
                    bukaKeran.setChecked(statusKeran);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Handle perubahan status switch
        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            keran.setValue(isChecked);
        });
    }
}