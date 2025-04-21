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
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    private DatabaseReference notifRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);
        LinearLayout notifContainer = view.findViewById(R.id.notifContainer);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // User belum login
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

        // Kalau user sudah login
        notifRef = FirebaseDatabase.getInstance().getReference("notifikasi");

        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (notifContainer == null || getContext() == null) {
                    return; // Jangan lanjut jika context atau notifContainer null
                }

                notifContainer.removeAllViews();

                List<DataSnapshot> listNotif = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    listNotif.add(data);
                }

                // Urutkan agar terbaru di atas (jika kamu simpan secara urut di Firebase, ini bisa dibalik aja)
                Collections.reverse(listNotif);

                for (DataSnapshot data : listNotif) {
                    String email = data.child("email").getValue() != null ? data.child("email").getValue().toString() : "-";
                    String pesan = data.child("pesan").getValue() != null ? data.child("pesan").getValue().toString() : "-";
                    String jam = data.child("jam").getValue() != null ? data.child("jam").getValue().toString() : "-";
                    String tanggal = data.child("tanggal").getValue() != null ? data.child("tanggal").getValue().toString() : "-";

                    // Pastikan getContext() tidak null sebelum membuat LinearLayout
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error jika perlu
            }
        });

        return view;
    }
}
