package com.bangraja.smartwatertank.view;

import com.bangraja.smartwatertank.R;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bangraja.smartwatertank.controller.MonitoringController;
import com.bangraja.smartwatertank.model.UkuranModel;
import com.bangraja.smartwatertank.view.custom.CustomMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import android.content.Context;

public class StatisticsAndHistoryFragment extends Fragment {

    private LineChart lineChart;
    Spinner filterSpinner;
    Spinner filterHistorySpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_statistik, container, false);

        lineChart = rootView.findViewById(R.id.lineChart);
        lineChart.setNoDataText("");

        filterSpinner = rootView.findViewById(R.id.filterSpinner);
        filterHistorySpinner = rootView.findViewById(R.id.filterHistorySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.filter_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        filterSpinner.setAdapter(adapter);
        filterHistorySpinner.setAdapter(adapter);

        UkuranModel ukuranModel = new UkuranModel();
        MonitoringController monitoringController = new MonitoringController(ukuranModel);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                monitoringController.statisticData(selectedFilter, lineChart, requireContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        filterHistorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                monitoringController.historyData(selectedFilter, requireContext(), rootView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return rootView;
    }

}