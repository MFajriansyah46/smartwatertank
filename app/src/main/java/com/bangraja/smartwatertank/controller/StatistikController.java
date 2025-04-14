package com.bangraja.smartwatertank.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bangraja.smartwatertank.R; // pastikan ini sesuai strukturmu

public class StatistikController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik); // pastikan nama file xml benar

        LineChart lineChart = findViewById(R.id.lineChart);

        // Generate data random (simulasi isi toren air)
        List<Entry> entries = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            float x = i; // bisa diubah jadi waktu/label nantinya
            float y = 20 + random.nextFloat() * 80; // nilai antara 20 - 100
            entries.add(new Entry(x, y));

            // Cek apakah data sudah benar
            Log.d("StatistikController", "x: " + x + ", y: " + y);
        }

        // Buat dataset untuk LineChart
        LineDataSet dataSet = new LineDataSet(entries, "Persentase Isi Toren");
        dataSet.setColor(Color.CYAN);                   // Warna garis
        dataSet.setValueTextColor(Color.WHITE);         // Warna teks nilai
        dataSet.setLineWidth(2f);                       // Ketebalan garis
        dataSet.setCircleRadius(4f);                    // Radius titik
        dataSet.setCircleColor(Color.WHITE);            // Warna titik

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData); // Pasang data ke chart

        // Styling tambahan
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getLegend().setTextColor(Color.WHITE);

        lineChart.invalidate(); // Refresh chart
    }
}
