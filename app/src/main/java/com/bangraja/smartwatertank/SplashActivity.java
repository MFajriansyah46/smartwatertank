package com.bangraja.smartwatertank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashIcon = findViewById(R.id.splash_icon);
        ProgressBar loadingBar = findViewById(R.id.loading_bar);

        // Set alpha ke 0 (invisible)
        splashIcon.setAlpha(0f);
        loadingBar.setAlpha(0f);

        // Animasi fade-in
        splashIcon.animate().alpha(1f).setDuration(800).start();
        loadingBar.animate().alpha(1f).setDuration(800).start();

        // Lanjut ke LoginActivity setelah delay
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish(); // agar tidak bisa kembali ke splash
        }, SPLASH_DURATION);
    }
}
