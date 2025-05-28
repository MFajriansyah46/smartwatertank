package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.model.AuthModel;
import com.bangraja.smartwatertank.model.CommandModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private TextView emailView, nameView;
    private ImageView backIcon;
    private CircleImageView imageView;
    private Switch bukaKeranOtomatis;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bread));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.meat));

        emailView = findViewById(R.id.emailView);
        nameView = findViewById(R.id.nameView);
        imageView = findViewById(R.id.imageView);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);
        logoutBtn = findViewById(R.id.logoutBtn);
        backIcon = findViewById(R.id.backIcon);

        backIcon.setOnClickListener(v -> onBackPressed());
        new AuthController(new AuthModel()).displayUserProfile(emailView, nameView, imageView);
        new CommandController(new CommandModel()).autoSwitch(bukaKeranOtomatis);
        logoutBtn.setOnClickListener(view -> new AuthController(new AuthModel()).logout(SettingsActivity.this));
    }
}