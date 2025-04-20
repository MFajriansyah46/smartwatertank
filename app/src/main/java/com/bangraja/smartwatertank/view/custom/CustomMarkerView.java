package com.bangraja.smartwatertank.view.custom;

import android.content.Context;
import android.widget.TextView;

import com.bangraja.smartwatertank.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomMarkerView extends MarkerView {

    private final TextView markerText;
    private final List<DocumentSnapshot> documents;

    public CustomMarkerView(Context context, List<DocumentSnapshot> documents) {
        super(context, R.layout.custom_marker_view);
        this.documents = documents;
        markerText = findViewById(R.id.markerText);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int index = (int) e.getX();
        if (index >= 0 && index < documents.size()) {
            DocumentSnapshot doc = documents.get(index);
            Date date = doc.getTimestamp("timestamp").toDate();

            String formatted = new SimpleDateFormat("HH.mm dd MMM yyyy", Locale.getDefault()).format(date);
            markerText.setText(formatted);
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
