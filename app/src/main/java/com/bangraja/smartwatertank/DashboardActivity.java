package com.bangraja.smartwatertank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Dashboard Fragment
public class DashboardActivity extends Fragment {
    private TextView pressure, height, water_volume;
    private Switch bukaKeran;
    private DatabaseReference transmiter, perintah;

    private ValueEventListener transmiterListener, perintahListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        pressure = view.findViewById(R.id.pressure);
        height = view.findViewById(R.id.height);
        water_volume = view.findViewById(R.id.water_volume);
        bukaKeran = view.findViewById(R.id.bukaKeran);

        transmiter = FirebaseDatabase.getInstance().getReference("transmiter");
        perintah = FirebaseDatabase.getInstance().getReference("perintah");

        transmiterListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                if (!snapshot.exists()) {
                    pressure.setText("No data");
                    height.setText("No data");
                    water_volume.setText("No data");
                    return;
                }

                Double heightValue = snapshot.child("height").getValue(Double.class);
                Long pressureValue = snapshot.child("pressure").getValue(Long.class);
                Double waterVolumeValue = snapshot.child("water_volume").getValue(Double.class);

                pressure.setText(pressureValue != null ? pressureValue + "" : "N/A");
                height.setText(heightValue != null ? String.format("%.2f", heightValue) : "N/A");
                water_volume.setText(waterVolumeValue != null ? String.format("%.2f", waterVolumeValue) : "N/A");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded() || getActivity() == null) return;
                pressure.setText("Error loading data");
                height.setText("Error loading data");
                water_volume.setText("Error loading data");
            }
        };
        transmiter.addValueEventListener(transmiterListener);

        perintahListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                Boolean statusKeran = snapshot.child("keran").getValue(Boolean.class);
                bukaKeran.setChecked(statusKeran != null && statusKeran);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded() || getActivity() == null) return;
            }
        };
        perintah.addValueEventListener(perintahListener);

        bukaKeran.setOnCheckedChangeListener((buttonView, isChecked) -> perintah.child("keran").setValue(isChecked));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (transmiterListener != null) {
            transmiter.removeEventListener(transmiterListener);
        }
        if (perintahListener != null) {
            perintah.removeEventListener(perintahListener);
        }
    }
}
