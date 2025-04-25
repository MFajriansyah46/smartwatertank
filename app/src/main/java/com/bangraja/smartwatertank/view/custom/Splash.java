package com.bangraja.smartwatertank.view.custom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bangraja.smartwatertank.Main;
import com.bangraja.smartwatertank.R;

public class Splash extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.meat));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.meat));

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
            startActivity(new Intent(Splash.this, Main.class));
            finish();
        }, SPLASH_DURATION);
    }
}
