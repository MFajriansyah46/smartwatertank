package com.bangraja.smartwatertank.view;

import com.bangraja.smartwatertank.R;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.List;

public class AnalysisFragment extends Fragment {

    private LineChart lineChart;
    Spinner filterSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistik, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        filterSpinner = view.findViewById(R.id.filterSpinner);

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
                        handleDataForChart(queryDocumentSnapshots.getDocuments());
                    });
            filterSpinner.setSelection(1);
        } else {
            ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
                handleDataForChart(queryDocumentSnapshots.getDocuments());
            });
        }
    }
    private void handleDataForChart(List<DocumentSnapshot> documents) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Collections.sort(documents, (d1, d2) -> {
            Date date1 = d1.getTimestamp("timestamp").toDate();
            Date date2 = d2.getTimestamp("timestamp").toDate();
            return date1.compareTo(date2);
        });

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < documents.size(); i++) {
            DocumentSnapshot doc = documents.get(i);
            Double volume = doc.getDouble("water_volume");
            Date date = doc.getTimestamp("timestamp").toDate();

            if (volume != null && date != null) {
                entries.add(new Entry(i, volume.floatValue()));
                labels.add(sdf.format(date));
            }
        }
        renderChart((ArrayList<Entry>) entries, (ArrayList<String>) labels);
    }
    private void renderChart(ArrayList<Entry> entries, ArrayList<String> labels) {
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
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });

        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setDrawGridBackground(false);

        Description desc = new Description();
        desc.setText("Volume Air per 15 Menit");
        desc.setTextColor(Color.WHITE);
        desc.setTextSize(12f);
        lineChart.setDescription(desc);
        lineChart.invalidate();
    }
}