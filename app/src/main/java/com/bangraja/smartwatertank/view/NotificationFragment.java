package com.bangraja.smartwatertank.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bangraja.smartwatertank.Main;
import com.bangraja.smartwatertank.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {
    private CollectionReference notifRef;
    private FirebaseFirestore db;

    // Pastikan Log diimpor
    private static final String TAG = "NotificationFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);
        LinearLayout notifContainer = view.findViewById(R.id.notifContainer);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null || getContext() == null) {
            TextView warning = new TextView(getContext());
            warning.setText("Silakan login untuk melihat notifikasi.");
            warning.setTextColor(Color.WHITE);
            warning.setTextSize(16);
            warning.setPadding(24, 24, 24, 24);
            notifContainer.addView(warning);
            return view;
        }

        String userEmail = currentUser.getEmail();
        db = FirebaseFirestore.getInstance();
        notifRef = db.collection("tb_notifikasi");

        notifRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, exception) -> {
                    if (notifContainer == null || getContext() == null || snapshots == null) return;

                    Log.d(TAG, "Snapshots diterima, jumlah dokumen: " + snapshots.size());
                    notifContainer.removeAllViews();

                    int unreadCount = 0;
                    List<DocumentSnapshot> listNotif = snapshots.getDocuments();

                    for (DocumentSnapshot document : listNotif) {
                        Map<String, Object> data = document.getData();
                        if (data == null) continue;

                        String email = String.valueOf(data.get("email"));
                        String pesan = String.valueOf(data.get("pesan"));
                        String jam = String.valueOf(data.get("jam"));
                        String tanggal = String.valueOf(data.get("tanggal"));
                        Boolean isRead = data.get("isRead") != null && (Boolean) data.get("isRead");

                        Log.d(TAG, "Notifikasi diterima: " + pesan);

                        if (!email.equals(userEmail)) continue;

                        boolean unread = isRead == null || !isRead;
                        if (unread) {
                            unreadCount++;
                        }

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

                        if (unread) {
                            View dot = new View(getContext());
                            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(16, 16);
                            dotParams.setMargins(0, 0, 0, 8);
                            dot.setLayoutParams(dotParams);
                            dot.setBackgroundResource(R.drawable.unread_dot);
                            card.addView(dot);

                            final DocumentReference notifDocRef = document.getReference();
                            card.setOnClickListener(v -> {
                                notifDocRef.update("isRead", true)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Status isRead berhasil diperbarui"))
                                        .addOnFailureListener(updateException -> Log.e(TAG, "Gagal update status isRead", updateException));
                                updateBadge();
                            });
                        }

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

                    updateBadge();
                });

        return view;
    }

    private void updateBadge() {
        if (getActivity() instanceof Main) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();
                db.collection("tb_notifikasi")
                        .whereEqualTo("email", email)
                        .whereEqualTo("isRead", false)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int unreadCount = task.getResult().size();
                                ((Main) getActivity()).updateNotifBadge(unreadCount);
                            }
                        });
            }
        }
    }
}
