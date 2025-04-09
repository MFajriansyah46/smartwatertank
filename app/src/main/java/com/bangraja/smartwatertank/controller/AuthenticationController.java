package com.bangraja.smartwatertank.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangraja.smartwatertank.MainActivity;
import com.bangraja.smartwatertank.model.AuthenticationModel;
import com.bangraja.smartwatertank.view.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AuthenticationController {
    private final AuthenticationModel am;

    public AuthenticationController() {
        am = new AuthenticationModel();
    }

    public void login(String email, String password, Activity activity, ProgressBar progressBar) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Email dan Password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = am.getAuth();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Intent intent = new Intent(activity, MainActivity.class);
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

    // Metode untuk menampilkan email pengguna pada TextView
    public void displayUserEmail(TextView emailTextView) {
        if (am.getCurrentUser() != null) {
            String email = am.getCurrentUser().getEmail();
            emailTextView.setText(email);
        } else {
            emailTextView.setText("Tidak ada akun login");
        }
    }
}