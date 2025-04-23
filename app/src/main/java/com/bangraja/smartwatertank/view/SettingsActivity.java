package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.model.AuthModel;
import com.bangraja.smartwatertank.model.CommandModel;

public class SettingsActivity extends AppCompatActivity {
    private TextView emailTextView;
    private Switch bukaKeranOtomatis;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        emailTextView = findViewById(R.id.emailView);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);
        logoutBtn = findViewById(R.id.logoutBtn);

        new AuthController(new AuthModel()).displayUserEmail(emailTextView);
        new CommandController(new CommandModel()).autoSwitch(bukaKeranOtomatis);
        logoutBtn.setOnClickListener(view -> new AuthController(new AuthModel()).logout(SettingsActivity.this));
    }
}