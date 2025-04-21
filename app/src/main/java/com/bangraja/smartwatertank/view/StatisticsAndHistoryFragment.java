package com.bangraja.smartwatertank.view;

import com.bangraja.smartwatertank.R;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bangraja.smartwatertank.controller.MonitoringController;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistik, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setNoDataText("");

        filterSpinner = view.findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.filter_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                fetchDataWithFilter(selectedFilter); // filter berdasarkan waktu
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void fetchDataWithFilter(String filter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("tb_ukuran");

        Date startDate = null;
        Calendar calendar = Calendar.getInstance();

        switch (filter) {
            case "Hari ini":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                startDate = calendar.getTime();
                break;

            case "Bulan ini":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                startDate = calendar.getTime();
                break;

            case "Tahun ini":
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                startDate = calendar.getTime();
                break;

            case "Semua":
                startDate = null; // ambil semua
                break;

        }

        if (startDate != null) {
            ref.whereGreaterThanOrEqualTo("timestamp", startDate)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        handleDataForChart15Menit(queryDocumentSnapshots.getDocuments(), filter); // <-- ini
                    });
        } else {
            ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
                handleDataForChart15Menit(queryDocumentSnapshots.getDocuments(), filter); // <-- ini
            });
        }
    }
    private void handleDataForChart(List<DocumentSnapshot> documents, String filter) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Collections.sort(documents, (d1, d2) -> {
            Date date1 = d1.getTimestamp("timestamp").toDate();
            Date date2 = d2.getTimestamp("timestamp").toDate();
            return date1.compareTo(date2);
        });

        SimpleDateFormat sdf;
        if (filter.equals("Hari ini")) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat(""); // kosongkan label X untuk filter lain
        }

        for (int i = 0; i < documents.size(); i++) {
            DocumentSnapshot doc = documents.get(i);
            Double volume = doc.getDouble("water_volume");
            Date date = doc.getTimestamp("timestamp").toDate();

            if (volume != null && date != null) {
                entries.add(new Entry(i, volume.floatValue()));
                labels.add(sdf.format(date));
            }
        }

        renderChart((ArrayList<Entry>) entries, (ArrayList<String>) labels, documents);

    }

    private void handleDataForChart15Menit(List<DocumentSnapshot> documents, String filter) {
        MonitoringController controller = new MonitoringController();
        List<String> labels = new ArrayList<>();
        List<Entry> entries = controller.statisticData(documents, filter, labels);

        renderChart((ArrayList<Entry>) entries, (ArrayList<String>) labels, documents);
    }

    private void renderChart(ArrayList<Entry> entries, ArrayList<String> labels, List<DocumentSnapshot> documents) {
        LineDataSet dataSet = new LineDataSet(entries, "Volume Air (L)");
        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });

        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setDrawGridBackground(false);

        CustomMarkerView marker = new CustomMarkerView(requireContext(), documents);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);

        lineChart.invalidate();

    }
}

