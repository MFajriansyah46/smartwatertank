package com.bangraja.smartwatertank.controller;

import android.content.Context;
import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.bangraja.smartwatertank.model.UkuranModel;
import com.bangraja.smartwatertank.view.custom.CustomMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.github.mikephil.charting.data.Entry;
import com.bangraja.smartwatertank.model.TransmiterModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class MonitoringController {
    private final UkuranModel um;

    public MonitoringController(UkuranModel um) {
        this.um = um;
    }

public void statisticData(String filter, LineChart lineChart, Context context) {

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
            startDate = null;
            break;
    }

    if (startDate != null) {
        um.getUkuranRef().whereGreaterThanOrEqualTo("timestamp", startDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    updateChartWithDocuments(documents, filter, lineChart, context);
                });
    } else {
        um.getUkuranRef().get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    updateChartWithDocuments(documents, filter, lineChart, context);
                });
    }
}

private void updateChartWithDocuments(List<DocumentSnapshot> documents, String filter, LineChart lineChart, Context context) {
    List<Entry> entries = new ArrayList<>();
    List<String> labels = new ArrayList<>();

    Collections.sort(documents, (d1, d2) -> {
        Date date1 = d1.getTimestamp("timestamp").toDate();
        Date date2 = d2.getTimestamp("timestamp").toDate();
        return date1.compareTo(date2);
    });

    SimpleDateFormat sdf;
    switch (filter) {
        case "Hari ini":
            sdf = new SimpleDateFormat("HH:mm");
            break;
        case "Bulan ini":
            sdf = new SimpleDateFormat("d MMM");
            break;
        case "Tahun ini":
            sdf = new SimpleDateFormat("MMM yyyy");
            break;
        default:
            sdf = new SimpleDateFormat("dd/MM/yyyy");
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

    CustomMarkerView marker = new CustomMarkerView(context, documents);
    marker.setChartView(lineChart);
    lineChart.setMarker(marker);

    lineChart.invalidate();
}

    public void historyData() {

    }
}
