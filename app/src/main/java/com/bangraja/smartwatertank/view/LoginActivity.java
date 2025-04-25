package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;
import com.bangraja.smartwatertank.model.AuthModel;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, exitBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.meat));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.meat));

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        exitBtn = findViewById(R.id.exitBtn);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(view -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();
            new AuthController(new AuthModel()).login(userEmail, userPassword, LoginActivity.this, progressBar);
        });

        exitBtn.setOnClickListener(view -> finishAffinity());
    }
}
