package com.bangraja.smartwatertank.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bangraja.smartwatertank.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {
    private CollectionReference notifRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);
        LinearLayout notifContainer = view.findViewById(R.id.notifContainer);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            if (getContext() != null) {
                TextView warning = new TextView(getContext());
                warning.setText("Silakan login untuk melihat notifikasi.");
                warning.setTextColor(Color.WHITE);
                warning.setTextSize(16);
                warning.setPadding(24, 24, 24, 24);
                notifContainer.addView(warning);
            }
            return view;
        }

        notifRef = FirebaseFirestore.getInstance().collection("tb_notifikasi");

        notifRef.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (notifContainer == null || getContext() == null) {
                    return;
                }

                notifContainer.removeAllViews();

                if (snapshots == null) return;

                List<DocumentSnapshot> listNotif = new ArrayList<>(snapshots.getDocuments());

                for (DocumentSnapshot document : listNotif) {
                    Map<String, Object> data = document.getData();
                    if (data == null) continue;

                    String email = data.get("email") != null ? data.get("email").toString() : "-";
                    String pesan = data.get("pesan") != null ? data.get("pesan").toString() : "-";
                    String jam = data.get("jam") != null ? data.get("jam").toString() : "-";
                    String tanggal = data.get("tanggal") != null ? data.get("tanggal").toString() : "-";

                    if (getContext() != null) {
                        LinearLayout card = new LinearLayout(getContext());
                        card.setOrientation(LinearLayout.VERTICAL);
                        card.setPadding(24, 24, 24, 24);
                        card.setBackgroundResource(R.drawable.rounded_box);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, 24);
                        card.setLayoutParams(params);

                        TextView emailText = new TextView(getContext());
                        emailText.setText(email);
                        emailText.setTextColor(Color.WHITE);
                        emailText.setTextSize(16);
                        emailText.setPadding(0, 0, 0, 8);
                        emailText.setTypeface(null, android.graphics.Typeface.BOLD);

                        TextView pesanText = new TextView(getContext());
                        pesanText.setText(pesan);
                        pesanText.setTextColor(Color.parseColor("#E0E0E0"));
                        pesanText.setTextSize(14);
                        pesanText.setPadding(0, 0, 0, 8);

                        TextView jamText = new TextView(getContext());
                        jamText.setText(jam + " • " + tanggal);
                        jamText.setTextColor(Color.parseColor("#E0E0E0"));
                        jamText.setTextSize(12);

                        card.addView(emailText);
                        card.addView(pesanText);
                        card.addView(jamText);

                        notifContainer.addView(card);
                    }
                }
            }
        });

        return view;
    }
}
