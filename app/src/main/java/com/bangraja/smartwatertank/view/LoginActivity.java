package com.bangraja.smartwatertank.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, exitBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        exitBtn = findViewById(R.id.exitBtn);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(view -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();
            new AuthController().login(userEmail, userPassword, LoginActivity.this, progressBar);
        });

        exitBtn.setOnClickListener(view -> finishAffinity());
    }
}
