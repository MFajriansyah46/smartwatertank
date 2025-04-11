package com.bangraja.smartwatertank;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bangraja.smartwatertank.view.DashboardFragment;
import com.bangraja.smartwatertank.view.LoginActivity;
import com.bangraja.smartwatertank.view.NotificationFragment;
import com.bangraja.smartwatertank.view.SettingsActivity;
import com.bangraja.smartwatertank.view.AnalysisFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Main extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bread));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.bread));

        // Cek apakah user sudah login
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(Main.this, LoginActivity.class));
            finish();
            return;
        }

        // Inisialisasi toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inisialisasi bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Dashboard");
                }
            } else if (id == R.id.nav_statistik) {
                selectedFragment = new AnalysisFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Statistik");
                }
            } else if (id == R.id.nav_notifikasi) {
                selectedFragment = new NotificationFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Notifikasi");
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Navigasi ke halaman pengaturan
            startActivity(new Intent(Main.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}