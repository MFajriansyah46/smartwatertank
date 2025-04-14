package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.DashboardController;

public class DashboardFragment extends Fragment {
    private TextView pressure, height, waterVolume;
    private Switch bukaKeran;
    private ProgressBar progressVolume;
    private TextView progressPercent;

    private DashboardController dc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        pressure = view.findViewById(R.id.pressure);
        height = view.findViewById(R.id.height);
        waterVolume = view.findViewById(R.id.water_volume);
        bukaKeran = view.findViewById(R.id.bukaKeran);
        progressVolume = view.findViewById(R.id.progressVolume);
        progressPercent = view.findViewById(R.id.progressPercent);

        dc = new DashboardController();
        dc.setupTransmiterListener(pressure, height, waterVolume,progressVolume, progressPercent);
        dc.setupPerintahListener(bukaKeran);

        return view;
    }
}
