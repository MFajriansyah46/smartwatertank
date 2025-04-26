package com.bangraja.smartwatertank.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.Main;
import com.bangraja.smartwatertank.model.AuthModel;
import com.bangraja.smartwatertank.view.LoginActivity;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthController {
    private final AuthModel am;

    public AuthController(AuthModel am) {
        this.am = am;
    }

    public void login(String email, String password, Activity activity, ProgressBar progressBar) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Email dan Password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        am.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Intent intent = new Intent(activity, Main.class);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Toast.makeText(activity, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout(Activity activity) {
        am.getAuth().signOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public void displayUserProfile(TextView emailView, TextView nameView, CircleImageView imageView) {
        if (am.getCurrentUser() != null) {
            String email = am.getEmail();
            emailView.setText(email);

            am.getProfile()
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            QueryDocumentSnapshot doc = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);

                            String nama = doc.getString("nama");
                            String gambar = doc.getString("gambar");

                            if (nama != null) {
                                nameView.setText(nama);
                            }

                            if (gambar != null && !gambar.isEmpty()) {
                                try {
                                    AssetManager assetManager = imageView.getContext().getAssets();
                                    InputStream is = assetManager.open(gambar);
                                    Drawable drawable = Drawable.createFromStream(is, null);
                                    imageView.setImageDrawable(drawable);
                                } catch (IOException e) {
                                    imageView.setImageResource(R.drawable.blank_profile); // fallback jika gagal
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(emailView.getContext(), "Gagal memuat profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            emailView.setText("Tidak ada akun login");
        }
    }
}