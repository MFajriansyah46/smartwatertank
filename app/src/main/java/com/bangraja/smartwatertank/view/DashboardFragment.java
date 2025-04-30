package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.controller.DashboardController;
import com.bangraja.smartwatertank.model.CommandModel;
import com.bangraja.smartwatertank.model.TransmiterModel;

public class DashboardFragment extends Fragment {
    private TextView pressure, height, waterVolume, progressPercent;
    private Switch bukaKeran;
    private LinearLayout switchContainer;
    private ProgressBar progressVolume;
    private View view, riverEffect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_dashboard, container, false);

        pressure = view.findViewById(R.id.pressure);
        height = view.findViewById(R.id.height);
        waterVolume = view.findViewById(R.id.water_volume);
        progressVolume = view.findViewById(R.id.progressVolume);
        progressPercent = view.findViewById(R.id.progressPercent);
        bukaKeran = view.findViewById(R.id.bukaKeran);
        switchContainer = view.findViewById(R.id.switchContainer);
        riverEffect = view.findViewById(R.id.riverEffect);

        new DashboardController(new TransmiterModel()).realtimeData(pressure, height, waterVolume,progressVolume, progressPercent);
        new CommandController(new CommandModel()).manualSwitch(bukaKeran, riverEffect, switchContainer);

        return view;
    }
}
