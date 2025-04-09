package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthenticationController;
import com.bangraja.smartwatertank.controller.CommandController;

public class SettingsActivity extends AppCompatActivity {
    private TextView emailTextView;
    private Switch bukaKeranOtomatis;
    private Button logoutBtn;
    private AuthenticationController ac;
    private CommandController cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ac = new AuthenticationController();
        cc = new CommandController();

        emailTextView = findViewById(R.id.emailView);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);
        logoutBtn = findViewById(R.id.logoutBtn);

        ac.displayUserEmail(emailTextView);
        cc.autoSwitch(bukaKeranOtomatis, this);
        logoutBtn.setOnClickListener(view -> ac.logout(SettingsActivity.this));
    }
}